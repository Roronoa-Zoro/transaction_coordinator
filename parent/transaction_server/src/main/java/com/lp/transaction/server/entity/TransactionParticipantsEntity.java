package com.lp.transaction.server.entity;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lp.transaction.client.enums.TransactionState;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by 123 on 2016/8/2.
 */
@Data
@TableName("transaction_participants")
public class TransactionParticipantsEntity {
    @TableId(value="participants_id", type= IdType.AUTO)
    private Long participantsId;
    @TableField("trx_id")
    private Long trxId;
    @TableField("participants_state")
    private Integer participantsState;
    @TableField("participants_callback")
    private String participantsCallback;
    @TableField("participants_submit_callback")
    private String participantsSubmitCallback;
    @TableField("participants_rollback_callback")
    private String participantsRollbackCallback;
    @TableField("participants_args")
    private String participantsArgs;
    @TableField("participants_create_time")
    private LocalDateTime participantsCreateTime;
    @TableField("participants_update_time")
    private LocalDateTime participantsUpdateTime;
    @TableField("participants_version")
    private Integer participantsVersion;
    @TableField("participants_invoke_state")
    private Integer participantsInvokeState;
}
