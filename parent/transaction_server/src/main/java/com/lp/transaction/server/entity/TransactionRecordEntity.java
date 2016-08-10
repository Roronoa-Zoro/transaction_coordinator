package com.lp.transaction.server.entity;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lp.transaction.client.enums.TransactionState;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("transaction_record")
public class TransactionRecordEntity {
    @TableId(value="trx_id", type= IdType.AUTO)
    private Long trxId;
    @TableField("trx_state")
    private Integer trxState;
    //事务参与者的数量
    @TableField("trx_parti_num")
    private Integer trxPartiNum;
    @TableField("trx_callback")
    private String trxCallback;
    @TableField("trx_submit_callback")
    private String trxSubmitCallback;
    @TableField("trx_rollback_callback")
    private String trxRollbackCallback;
    @TableField("trx_create_time")
    private LocalDateTime trxCreateTime;
    @TableField("trx_update_time")
    private LocalDateTime trxUpdateTime;
    @TableField("trx_version")
    private Integer trxVersion;
    @TableField("trx_process_status")
	private Integer trxProcessStatus;
    @TableField("trx_callback_invoke_status")
    private Integer trxCallbackInvokeStatus;
}
