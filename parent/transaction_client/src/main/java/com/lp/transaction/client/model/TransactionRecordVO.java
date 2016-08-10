package com.lp.transaction.client.model;

import com.lp.transaction.client.enums.TransactionState;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TransactionRecordVO implements Serializable{

    private Long trxId;
    //事务参与者的数量
    private int trxPartiNum;
    private String trxCallback;
    private String trxSubmitCallback;
    private String trxRollbackCallback;
}
