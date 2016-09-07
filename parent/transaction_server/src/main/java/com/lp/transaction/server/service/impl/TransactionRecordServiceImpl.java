package com.lp.transaction.server.service.impl;

import com.baomidou.framework.service.impl.SuperServiceImpl;
import com.baomidou.mybatisplus.plugins.Page;
import com.lp.transaction.client.enums.TransactionState;
import com.lp.transaction.server.dao.TransactionRecordMapper;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TopicInfo;
import com.lp.transaction.server.enums.TrxBusinessState;
import com.lp.transaction.server.enums.TrxMsgSendResState;
import com.lp.transaction.server.enums.TrxMsgProcessState;
import com.lp.transaction.server.msg.MessageWraper;
import com.lp.transaction.server.msg.sender.DefaultRocketMqProducer;
import com.lp.transaction.server.service.TransactionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 123 on 2016/8/2.
 */
@Service
@Slf4j
public class TransactionRecordServiceImpl extends SuperServiceImpl<TransactionRecordMapper, TransactionRecordEntity>
                implements TransactionRecordService {

    @Autowired
    private DefaultRocketMqProducer producer;

    @Override
    public TrxMsgSendResState sendTrxParticipantMsg() {
        Page<TransactionRecordEntity> page = new Page<>(0, 500);
        TransactionRecordEntity record = new TransactionRecordEntity();
        record.setProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
        Page<TransactionRecordEntity> recordPage = super.selectPage(page, record);
        if (recordPage.getRecords() == null || recordPage.getRecords().isEmpty()) {
            log.info("did not get message");
            return TrxMsgSendResState.SEND_WITHOUT_MSG;
        }
        final AtomicInteger counter = new AtomicInteger(0);

            recordPage.getRecords().forEach(r -> {
                try{
                    MessageWraper<TransactionRecordEntity> wraper = new MessageWraper<>();
                    wraper.setMsg(r);
                    wraper.setUuid(String.valueOf(r.getTrxId()));
                    if (TransactionState.COMMIT_ALLOWED.getState() == r.getTrxState()) {
                        wraper.setType(TopicInfo.CommitTopic);
                    } else if (TransactionState.ROLLBACK.getState() == r.getTrxState()) {
                        wraper.setType(TopicInfo.RollbackTopic);
                    } else if (TransactionState.UNKNOWN.getState() == r.getTrxState()) {
                        wraper.setType(TopicInfo.UnkownTopic);
                    }
                    boolean res = producer.send(wraper);
                    if (res) {
                        r.setProcessStatus(TrxMsgProcessState.Process_Done.getStatus());
                        r.setUpdateTime(LocalDateTime.now());
                        super.updateById(r);
                        log.debug("trx msg:{} sent out success", r.getTrxId());
                        counter.incrementAndGet();
                    }

                }catch (Exception e)  {
                    log.error("send trx msg:{} fails", r.getTrxId(), e);
                }

            });
        if (counter.get() == recordPage.getRecords().size()) {
            log.info("send all msg success in this batch");
            return TrxMsgSendResState.SEND_SUCCESS;
        }
        log.info("some trx message:{} not deliver", (recordPage.getRecords().size() - counter.get()));
        return TrxMsgSendResState.SEND_FAILURE;
    }

    @Override
    public boolean commitMainTrx(TransactionRecordEntity entity) {
        entity.setTrxState(TransactionState.COMMIT_ALLOWED.getState());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
        super.updateById(entity);
        return true;
    }

    @Override
    public boolean rollbackMainTrx(TransactionRecordEntity entity) {
        entity.setTrxState(TransactionState.ROLLBACK.getState());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setProcessStatus(TrxMsgProcessState.Process_Allowed.getStatus());
        super.updateById(entity);
        return true;
    }

    @Override
    public boolean updateMainTrxState(TransactionRecordEntity entity) {
        super.updateById(entity);
        return true;
    }
}
