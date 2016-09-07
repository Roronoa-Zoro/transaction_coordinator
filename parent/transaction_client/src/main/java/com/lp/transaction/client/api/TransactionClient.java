package com.lp.transaction.client.api;

import com.lp.transaction.client.model.TransactionParticipantsVO;
import com.lp.transaction.client.model.TransactionRecordVO;

public interface TransactionClient {

    TransactionRecordVO addTrxInitiator(TransactionRecordVO trx);

    boolean commitTrxInitiator(TransactionRecordVO trx);

    boolean rollbackTrxInitiator(TransactionRecordVO trx);

    TransactionParticipantsVO addTrxParticipant(TransactionParticipantsVO participant);

    boolean commitTrxParticipant(TransactionParticipantsVO trx);

    boolean rollbackTrxParticipant(TransactionParticipantsVO trx);
}
