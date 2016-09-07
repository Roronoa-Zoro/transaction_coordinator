package com.lp.transaction.server.converter;

import com.lp.transaction.client.enums.TransactionState;
import com.lp.transaction.client.model.TransactionParticipantsVO;
import com.lp.transaction.client.model.TransactionRecordVO;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TrxMsgProcessState;
import com.lp.transaction.server.enums.TrxParticipantType;

import java.time.LocalDateTime;

/**
 * Created by 123 on 2016/8/3.
 */
public class VOToEntity {
    public TransactionRecordEntity toTransactionRecordEntity(TransactionRecordVO vo) {
        TransactionRecordEntity entity = new TransactionRecordEntity();
        entity.setCallbackMonitorUrl(vo.getCallbackMonitorUrl());
        entity.setTrxPartiNum(vo.getTrxPartiNum());
        entity.setCallbackRollbackUrl(vo.getCallbackRollbackUrl());
        entity.setCallbackCommitUrl(vo.getCallbackSubmitUrl());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setVersion(0);
        entity.setTrxState(TransactionState.UNKNOWN.getState());
        entity.setProcessStatus(TrxMsgProcessState.Process_Not_Allowed.getStatus());
        entity.setTrxType(TrxParticipantType.TrxInitiator.getType());
        return entity;
    }

    public TransactionRecordEntity toTransactionParticipantsEntity(TransactionParticipantsVO vo) {
        TransactionRecordEntity entity = new TransactionRecordEntity();
        entity.setTrxState(TransactionState.UNKNOWN.getState());
        entity.setCallbackMonitorUrl(vo.getCallbackMonitorUrl());
        entity.setCallbackRollbackUrl(vo.getCallbackRollbackUrl());
        entity.setCallbackCommitUrl(vo.getCallbackSubmitUrl());
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setVersion(0);
        entity.setTrxId(vo.getParticipantsId());
        entity.setTrxInitiatorId(vo.getTrxId());
        entity.setTrxType(TrxParticipantType.TrxParticipant.getType());
        return entity;
    }
}
