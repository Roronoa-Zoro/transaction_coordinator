package com.lp.transaction.server.service;

import com.baomidou.framework.service.ISuperService;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TrxMsgSendResState;

/**
 * Created by 123 on 2016/8/2.
 */
public interface TransactionRecordService extends ISuperService<TransactionRecordEntity> {

    TrxMsgSendResState sendTrxParticipantMsg();

    boolean commitMainTrx(TransactionRecordEntity entity);

    boolean rollbackMainTrx(TransactionRecordEntity entity);

    boolean updateMainTrxState(TransactionRecordEntity entity);
}
