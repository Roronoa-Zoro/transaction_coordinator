package com.lp.transaction.server.rpc;

import com.lp.transaction.client.api.TransactionClient;
import com.lp.transaction.client.model.TransactionParticipantsVO;
import com.lp.transaction.client.model.TransactionRecordVO;
import com.lp.transaction.server.converter.VOToEntity;
import com.lp.transaction.server.entity.TransactionParticipantsEntity;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.service.TransactionParticipantsService;
import com.lp.transaction.server.service.TransactionRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by 123 on 2016/8/2.
 */
@Component
@Slf4j
public class DefaultTransactionClient implements TransactionClient {

    @Autowired
    private TransactionRecordService trxRecordService;
    @Autowired
    private TransactionParticipantsService trxParticipantService;
    private VOToEntity converter = new VOToEntity();

    @Override
    public TransactionRecordVO addMainTrx(TransactionRecordVO trx) {
        TransactionRecordEntity entity = converter.toTransactionRecordEntity(trx);
        boolean result = trxRecordService.insert(entity);
        log.info("insert one main trx record:{}, result:{}", entity.getTrxId(), result);
        trx.setTrxId(entity.getTrxId());
        return trx;
    }

    @Override
    public TransactionParticipantsVO addParticipantsTrx(TransactionParticipantsVO participant) {
        TransactionParticipantsEntity entity = converter.toTransactionParticipantsEntity(participant);
        boolean result = trxParticipantService.insert(entity);
        log.info("insert one participant trx trxId:{}, result:{}", participant.getTrxId(), result);
        participant.setParticipantsId(entity.getParticipantsId());
        return participant;
    }

    @Override
    public boolean commitMainTrx(TransactionRecordVO trx) {
        TransactionRecordEntity entity = converter.toTransactionRecordEntity(trx);
        entity.setTrxId(trx.getTrxId());
        //submit a task to async process
        return trxRecordService.commitMainTrx(entity);
    }

    @Override
    public boolean rollbackMainTrx(TransactionRecordVO trx) {
        TransactionRecordEntity entity = converter.toTransactionRecordEntity(trx);
        entity.setTrxId(trx.getTrxId());
        return trxRecordService.rollbackMainTrx(entity);
        //submit a task to async process
    }
}
