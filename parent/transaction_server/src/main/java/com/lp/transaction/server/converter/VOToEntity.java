package com.lp.transaction.server.converter;

import com.lp.transaction.client.enums.TransactionState;
import com.lp.transaction.client.model.TransactionParticipantsVO;
import com.lp.transaction.client.model.TransactionRecordVO;
import com.lp.transaction.server.entity.TransactionParticipantsEntity;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TrxBusinessState;
import com.lp.transaction.server.enums.TrxMsgProcessState;

import java.time.LocalDateTime;

/**
 * Created by 123 on 2016/8/3.
 */
public class VOToEntity {
    public TransactionRecordEntity toTransactionRecordEntity(TransactionRecordVO vo) {
        TransactionRecordEntity entity = new TransactionRecordEntity();
        entity.setTrxCallback(vo.getTrxCallback());
        entity.setTrxPartiNum(vo.getTrxPartiNum());
        entity.setTrxRollbackCallback(vo.getTrxRollbackCallback());
        entity.setTrxSubmitCallback(vo.getTrxSubmitCallback());
        entity.setTrxCreateTime(LocalDateTime.now());
        entity.setTrxUpdateTime(LocalDateTime.now());
        entity.setTrxVersion(0);
        entity.setTrxState(TransactionState.UNKNOWN.getState());
        entity.setTrxProcessStatus(TrxMsgProcessState.Process_Not_Allowed.getStatus());
        return entity;
    }

    public TransactionParticipantsEntity toTransactionParticipantsEntity(TransactionParticipantsVO vo) {
        TransactionParticipantsEntity entity = new TransactionParticipantsEntity();
        entity.setParticipantsState(TransactionState.UNKNOWN.getState());
        entity.setParticipantsArgs(vo.getParticipantsArgs());
        entity.setParticipantsCallback(vo.getParticipantsCallback());
        entity.setParticipantsCreateTime(LocalDateTime.now());
        entity.setParticipantsRollbackCallback(vo.getParticipantsRollbackCallback());
        entity.setParticipantsSubmitCallback(vo.getParticipantsSubmitCallback());
        entity.setParticipantsUpdateTime(LocalDateTime.now());
        entity.setParticipantsVersion(0);
        entity.setTrxId(vo.getTrxId());
        return entity;
    }
}
