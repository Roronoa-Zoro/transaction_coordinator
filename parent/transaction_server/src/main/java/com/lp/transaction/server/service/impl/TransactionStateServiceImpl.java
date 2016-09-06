package com.lp.transaction.server.service.impl;

import com.lp.transaction.client.enums.CallbackState;
import com.lp.transaction.client.enums.TransactionState;
import com.lp.transaction.server.entity.TransactionParticipantsEntity;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TrxMsgProcessState;
import com.lp.transaction.server.monitor.TrxParticipantMonitorPool;
import com.lp.transaction.server.service.RestService;
import com.lp.transaction.server.service.TransactionParticipantsService;
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
    private TransactionParticipantsService trxParticipantService;
    @Autowired
    private TrxParticipantMonitorPool pool;


    @Override
    public boolean handleUnknownState(TransactionRecordEntity trx) {
        CallbackState res = restService.monitorTrxStatus(trx.getTrxCallback(), trx.getTrxId());
        if (CallbackState.CallbackCommitFailure == res) {
            log.info("main trx:{} commit fails, will rollback", trx.getTrxId());
            trxRecordService.rollbackMainTrx(trx);
            return true;
        }

        TransactionRecordEntity dbTrx = trxRecordService.selectById(trx.getTrxId());
        TransactionParticipantsEntity participants = new TransactionParticipantsEntity();
        participants.setTrxId(trx.getTrxId());
        List<TransactionParticipantsEntity> participantsList = trxParticipantService.selectList(participants);
        if (dbTrx.getTrxPartiNum() != participantsList.size()) {
            trxRecordService.rollbackMainTrx(trx);
            log.info("check unknown record:{}, result is rollback due to actual participants num:{} is not equals to expected num:{}",
                    trx.getTrxId(), participantsList.size(), dbTrx.getTrxPartiNum());
            return true;
        }

        Set<Integer> stateSet = new HashSet<>();
        stateSet.add(res.getState());
        participantsList.forEach(par -> {
            CallbackState parRes = restService.monitorTrxStatus(par.getParticipantsCallback(), par.getParticipantsId());
            stateSet.add(parRes.getState());
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
        TransactionParticipantsEntity participants = new TransactionParticipantsEntity();
        participants.setTrxId(trx.getTrxId());
        List<TransactionParticipantsEntity> participantsList = trxParticipantService.selectList(participants);

        if (trx.getTrxPartiNum() != participantsList.size()) {
            log.info("some transaction participants are not in this transaction activity, rollback, trxId:{}", trx.getTrxId());
            trxRecordService.rollbackMainTrx(trx);
            return true;
        }

        //事务参与者的数量和预期的一样,表示没有参与者实际执行失败,但由于网络原因造成主事务接收到成功
        List<TransactionParticipantsEntity> processedList = new ArrayList<>();
        LocalDateTime updateTime = LocalDateTime.now();

        /**
         * 应该先检查所有参与者是否都处于可提交状态
         */
        List<Future<CallbackState>> futures = new ArrayList<>(participantsList.size());
        participantsList.forEach(participant -> {
                futures.add(pool.submitMonitorTask(participant.getParticipantsCallback(), participant.getParticipantsId()));
        });
        boolean invalidCommit = false;
        for (Future<CallbackState> future : futures) {
            if (CallbackState.CallbackPreCommit != future.get()) {
                //有参与者事务未处于可提交状态
                invalidCommit = true;
                break;
            }
        }
        if (invalidCommit) {
            trxRecordService.rollbackMainTrx(trx);
            return true;
        }

        ConcurrentMap<Future<CallbackInvokeResVO>, TransactionParticipantsEntity> invokeMap = new ConcurrentHashMap<>(participantsList.size());
        for (TransactionParticipantsEntity par : participantsList) {
            if (TransactionState.COMMIT.getState() == par.getParticipantsState()) {
                continue;
            }
            Future<CallbackInvokeResVO> future = pool.submitCommitOrRollbackTask(par.getParticipantsSubmitCallback(), par.getParticipantsId());
            invokeMap.put(future, par);
        }

        for (ConcurrentMap.Entry<Future<CallbackInvokeResVO>, TransactionParticipantsEntity> entry : invokeMap.entrySet()) {
            //所以处于可提交状态的回调都可以执行成功，本次不成功下次继续执行
            if (entry.getKey().get().getInvokeState() == CallbackState.CallbackCommitSuccess) {
                entry.getValue().setParticipantsState(TransactionState.COMMIT.getState());
                entry.getValue().setParticipantsUpdateTime(updateTime);
                entry.getValue().setParticipantsInvokeState(entry.getKey().get().getInvokeState().getState());
                processedList.add(entry.getValue());
            }
        }

        if (!processedList.isEmpty()) {
            trxParticipantService.updateBatchById(processedList);
        }
        if (invokeMap.keySet().size() != processedList.size()) {
            trx.setTrxProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
            trx.setTrxUpdateTime(LocalDateTime.now());
            trxRecordService.updateMainTrxState(trx);
        }
        log.info("all participants commit done");
        return true;

    }

    @Override
    public boolean handleRollbackState(TransactionRecordEntity trx) {
        TransactionParticipantsEntity participants = new TransactionParticipantsEntity();
        participants.setTrxId(trx.getTrxId());
        List<TransactionParticipantsEntity> participantsList = trxParticipantService.selectList(participants);
        if (participantsList == null || participantsList.isEmpty()) {
            log.info("there's no trx participants,trxId:{}", trx.getTrxId());
            return true;
        }
        LocalDateTime updateTime = LocalDateTime.now();
        //表示主事务还未回滚, 防止重复操作
        if (trx.getTrxCallbackInvokeStatus() != CallbackState.CallbackRollbackFailure.getState() && trx.getTrxCallbackInvokeStatus() != CallbackState.CallbackFailure.getState()) {
            //回滚主事务
            CallbackInvokeResVO mainRes = restService.invoke(trx.getTrxRollbackCallback(), trx.getTrxId());
            if (mainRes.getInvokeState() == CallbackState.CallbackRollbackFailure || mainRes.getInvokeState() == CallbackState.CallbackFailure) {
                trx.setTrxProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
                trx.setTrxUpdateTime(updateTime);
                log.info("rollback main trx:{} fails, reset it and process it later", trx.getTrxId());
                trxRecordService.updateById(trx);
                return true;
            }
            trx.setTrxCallbackInvokeStatus(mainRes.getInvokeState().getState());
            trx.setTrxUpdateTime(updateTime);
        }

        boolean allParticipantRollback = true;
        List<TransactionParticipantsEntity> processedList = new ArrayList<>();
        for (TransactionParticipantsEntity par : participantsList) {
            if (TransactionState.ROLLBACK.getState() == par.getParticipantsState()) {
                continue;
            }
            CallbackInvokeResVO res = restService.invoke(par.getParticipantsCallback(), par.getParticipantsId());
            if (res.getInvokeState() == CallbackState.CallbackRollbackFailure || res.getInvokeState() == CallbackState.CallbackFailure) {
                //此处回滚回掉失败
                log.warn("rollback callback:{}, arg:{} fails", par.getParticipantsCallback(), par.getParticipantsId());
                allParticipantRollback = false;
                continue;
            }
            par.setParticipantsState(TransactionState.ROLLBACK.getState());
            par.setParticipantsUpdateTime(updateTime);
            par.setParticipantsInvokeState(res.getInvokeState().getState());
            processedList.add(par);
        }
        //把已经回滚完成的进行更新
        if (!processedList.isEmpty()) {
            trxParticipantService.updateBatchById(processedList);
        }
        if (!allParticipantRollback) {
            trx.setTrxUpdateTime(updateTime);
            trx.setTrxProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
            log.info("some trx participants rollback fails, will reset and handle it later,trxId:{}", trx.getTrxId());
            trxRecordService.updateById(trx);
            return true;
        }

        log.info("all trx participants are rollback,trxId:{}", trx.getTrxId());
        return true;
    }
}
