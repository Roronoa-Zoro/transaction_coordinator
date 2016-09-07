package com.lp.transaction.client.model;

import com.lp.transaction.client.enums.TransactionState;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by 123 on 2016/8/2.
 */
@Data
public class TransactionParticipantsVO implements Serializable {
    private Long participantsId;
    private Long trxId;
    private String callbackMonitorUrl;
    private String callbackSubmitUrl;
    private String callbackRollbackUrl;
    private Integer trxSource;
}
