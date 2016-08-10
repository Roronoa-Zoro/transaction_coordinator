package com.lp.transaction.client.api;

import com.lp.transaction.client.model.TransactionParticipantsVO;
import com.lp.transaction.client.model.TransactionRecordVO;

public interface TransactionClient {

    TransactionRecordVO addMainTrx(TransactionRecordVO trx);

    TransactionParticipantsVO addParticipantsTrx(TransactionParticipantsVO participant);

    boolean commitMainTrx(TransactionRecordVO trx);

    boolean rollbackMainTrx(TransactionRecordVO trx);
}
