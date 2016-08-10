package com.lp.transaction.server.service;

import com.lp.transaction.server.entity.TransactionRecordEntity;

/**
 * Created by 123 on 2016/8/2.
 */
public interface TransactionStateService {
    boolean handleUnknownState(TransactionRecordEntity trx);

    boolean handleCommitState(TransactionRecordEntity trx);

    boolean handleRollbackState(TransactionRecordEntity trx);
}
