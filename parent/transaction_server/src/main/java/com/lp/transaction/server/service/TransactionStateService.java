package com.lp.transaction.server.service;

import com.lp.transaction.server.entity.TransactionRecordEntity;

import java.util.concurrent.ExecutionException;

/**
 * Created by 123 on 2016/8/2.
 */
public interface TransactionStateService {
    boolean handleUnknownState(TransactionRecordEntity trx) throws ExecutionException, InterruptedException;

    boolean handleCommitState(TransactionRecordEntity trx) throws ExecutionException, InterruptedException;

    boolean handleRollbackState(TransactionRecordEntity trx);
}
