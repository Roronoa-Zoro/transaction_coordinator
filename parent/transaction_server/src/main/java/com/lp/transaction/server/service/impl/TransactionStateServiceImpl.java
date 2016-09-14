package com.lp.transaction.server.service.impl;

import com.lp.transaction.client.enums.CallbackState;
import com.lp.transaction.client.enums.TransactionState;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TrxMsgProcessState;
import com.lp.transaction.server.util.TrxParticipantMonitorPool;
import com.lp.transaction.server.service.RestService;
import com.lp.transaction.server.service.TransactionRecordService;
import com.lp.transaction.server.service.TransactionStateService;
import com.lp.transaction.server.vo.CallbackInvokeResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 123 on 2016/8/2.
 */
@Service
@Slf4j
public class TransactionStateServiceImpl implements TransactionStateService {

    @Autowired
    private RestService restService;
    @Autowired
    private TransactionRecordService trxRecordService;
    @Autowired
    private TrxParticipantMonitorPool pool;


    @Override
    public boolean handleUnknownState(TransactionRecordEntity trx) throws ExecutionException, InterruptedException {
        TransactionState res = restService.monitorTrxStatus(trx.getCallbackMonitorUrl(), trx.getTrxId());
        if (TransactionState.UNKNOWN == res) {
            log.info("main trx:{} commit fails, will rollback", trx.getTrxId());
            trxRecordService.rollbackMainTrx(trx);
            return true;
        }

        TransactionRecordEntity dbTrx = trxRecordService.selectById(trx.getTrxId());
        TransactionRecordEntity participants = new TransactionRecordEntity();
        participants.setTrxInitiatorId(trx.getTrxId());
        List<TransactionRecordEntity> participantsList = trxRecordService.selectList(participants);
        if (dbTrx.getTrxPartiNum() != participantsList.size()) {
            trxRecordService.rollbackMainTrx(trx);
            log.info("check unknown record:{}, result is rollback due to actual participants num:{} is not equals to expected num:{}",
                    trx.getTrxId(), participantsList.size(), dbTrx.getTrxPartiNum());
            return true;
        }

        Set<Integer> stateSet = new HashSet<>();
        stateSet.add(res.getState());
        List<Future<TransactionState>> futures = new ArrayList<>(participantsList.size());
        participantsList.forEach(participant -> {
            futures.add(pool.submitMonitorTask(participant.getCallbackMonitorUrl(), participant.getTrxId()));
        });
        futures.forEach(future -> {
            try {
                stateSet.add(future.get().getState());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        if (stateSet.size() > 1) {
            //有事务参与者的提交状态和其他参与者不一致，回滚
            trxRecordService.rollbackMainTrx(trx);
            return true;
        }

        trxRecordService.commitMainTrx(trx);
        log.info("check unknown record:{}, result is commit", trx.getTrxId());
        return true;

    }

    @Override
    public boolean handleCommitState(TransactionRecordEntity trx) throws ExecutionException, InterruptedException {
        TransactionRecordEntity participants = new TransactionRecordEntity();
        participants.setTrxInitiatorId(trx.getTrxId());
        List<TransactionRecordEntity> participantsList = trxRecordService.selectList(participants);

        if (trx.getTrxPartiNum() != participantsList.size()) {
            log.info("some transaction participants are not in this transaction activity, rollback, trxId:{}", trx.getTrxId());
            trxRecordService.rollbackMainTrx(trx);
            return true;
        }

        //事务参与者的数量和预期的一样,表示没有参与者实际执行失败,但由于网络原因造成主事务接收到成功
        List<TransactionRecordEntity> processedList = new ArrayList<>();
        LocalDateTime updateTime = LocalDateTime.now();

        /**
         * 应该先检查所有参与者是否都处于可提交状态
         */
        List<Future<TransactionState>> futures = new ArrayList<>(participantsList.size());
        participantsList.forEach(participant -> {
            if (CallbackState.CallbackCommitSuccess.getState() == participant.getCallbackInvokeStatus()) {
                return;
            }
            futures.add(pool.submitMonitorTask(participant.getCallbackMonitorUrl(), participant.getTrxId()));
        });
        boolean invalidCommit = false;
        for (Future<TransactionState> future : futures) {
            if (TransactionState.UNKNOWN == future.get()) {
                //有参与者事务未处于可提交状态
                invalidCommit = true;
                break;
            }
        }
        if (invalidCommit) {
            trxRecordService.rollbackMainTrx(trx);
            return true;
        }

        ConcurrentMap<Future<CallbackInvokeResVO>, TransactionRecordEntity> invokeMap = new ConcurrentHashMap<>(participantsList.size());
        if (CallbackState.CallbackCommitSuccess.getState() != trx.getCallbackInvokeStatus()) {
            Future<CallbackInvokeResVO> future = pool.submitCommitOrRollbackTask(trx.getCallbackCommitUrl(), trx.getTrxId());
            invokeMap.put(future, trx);
        }

        for (TransactionRecordEntity par : participantsList) {
            if (CallbackState.CallbackCommitSuccess.getState() == par.getCallbackInvokeStatus()) {
                continue;
            }
            Future<CallbackInvokeResVO> future = pool.submitCommitOrRollbackTask(par.getCallbackCommitUrl(), par.getTrxId());
            invokeMap.put(future, par);
        }

        for (ConcurrentMap.Entry<Future<CallbackInvokeResVO>, TransactionRecordEntity> entry : invokeMap.entrySet()) {
            //所有处于可提交状态的回调都可以执行成功，本次不成功下次继续执行
            if (entry.getKey().get().getInvokeState() == CallbackState.CallbackCommitSuccess) {
                entry.getValue().setTrxState(TransactionState.COMMITTED.getState());
                entry.getValue().setUpdateTime(updateTime);
                entry.getValue().setCallbackInvokeStatus(entry.getKey().get().getInvokeState().getState());
                processedList.add(entry.getValue());
            }
        }

        if (!processedList.isEmpty()) {
            trxRecordService.updateBatchById(processedList);
        }
        if (invokeMap.keySet().size() != processedList.size()) {
            trx.setProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
            trx.setUpdateTime(LocalDateTime.now());
            trxRecordService.updateMainTrxState(trx);
        }
        log.info("all participants commit done");
        return true;

    }

    @Override
    public boolean handleRollbackState(TransactionRecordEntity trx) {
        TransactionRecordEntity participants = new TransactionRecordEntity();
        participants.setTrxInitiatorId(trx.getTrxId());
        List<TransactionRecordEntity> participantsList = trxRecordService.selectList(participants);
        if (participantsList == null || participantsList.isEmpty()) {
            log.info("there's no trx participants,trxId:{}", trx.getTrxId());
            return true;
        }
        LocalDateTime updateTime = LocalDateTime.now();
        boolean mainTrxRollback = true;
        //表示主事务还未回滚, 防止重复操作
        if (trx.getCallbackInvokeStatus() != CallbackState.CallbackRollbackSuccess.getState()) {
            //回滚主事务
            CallbackInvokeResVO mainRes = restService.invoke(trx.getCallbackRollbackUrl(), trx.getTrxId());
            trx.setCallbackInvokeStatus(mainRes.getInvokeState().getState());
            if (mainRes.getInvokeState() == CallbackState.CallbackRollbackFailure || mainRes.getInvokeState() == CallbackState.CallbackFailure) {
                log.info("rollback main trx:{} fails, reset it and process it later", trx.getTrxId());
                mainTrxRollback = false;
            }
        }

        boolean allParticipantRollback = true;
        List<TransactionRecordEntity> processedList = new ArrayList<>();
        for (TransactionRecordEntity par : participantsList) {
            if (TransactionState.ROLLBACK.getState() == par.getTrxState()) {
                continue;
            }
            CallbackInvokeResVO res = restService.invoke(par.getCallbackRollbackUrl(), par.getTrxId());
            if (res.getInvokeState() == CallbackState.CallbackRollbackFailure || res.getInvokeState() == CallbackState.CallbackFailure) {
                //此处回滚回掉失败
                log.warn("rollback callback:{}, arg:{} fails", par.getCallbackRollbackUrl(), par.getTrxId());
                allParticipantRollback = false;
                continue;
            }
            par.setTrxState(TransactionState.ROLLBACK.getState());
            par.setUpdateTime(updateTime);
            par.setCallbackInvokeStatus(res.getInvokeState().getState());
            processedList.add(par);
        }
        //把已经回滚完成的进行更新
        if (!processedList.isEmpty()) {
            trxRecordService.updateBatchById(processedList);
        }

        if (mainTrxRollback && allParticipantRollback) {
            trx.setUpdateTime(updateTime);
            log.info("main trx and all trx participants are rollback,trxId:{}", trx.getTrxId());
            trxRecordService.updateById(trx);
            return true;
        }

        //主事务或参与者事务 回滚失败，后续继续处理
        trx.setUpdateTime(updateTime);
        trx.setProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
        log.info("some trx participants rollback fails, will reset and handle it later,trxId:{}", trx.getTrxId());
        trxRecordService.updateById(trx);
        return true;

    }
}
