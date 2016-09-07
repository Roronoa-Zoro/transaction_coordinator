package com.lp.transaction.server.entity;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("transaction_record")
public class TransactionRecordEntity {
    @TableId(value="trx_id", type= IdType.AUTO)
    private Long trxId;
    @TableField("trx_state")
    private Integer trxState;
    //事务参与者的数量, 针对事务发起者
    @TableField("trx_parti_num")
    private Integer trxPartiNum;

    //事务发起者id, 针对事务参与者
    @TableField("trx_initiator_id")
    private Long trxInitiatorId;

    @TableField("trx_type") //发起者 还是 参与者
    private Integer trxType;

    @TableField("callback_monitor_url")
    private String callbackMonitorUrl;
    @TableField("callback_commit_url")
    private String callbackCommitUrl;
    @TableField("callback_rollback_url")
    private String callbackRollbackUrl;

    @TableField("version")
    private Integer version;
    @TableField("process_status")
	private Integer processStatus;
    @TableField("callback_invoke_status")
    private Integer callbackInvokeStatus;
    @TableField("trx_source") //事务来源, 即使用该系统的客户端
    private Integer trxSource;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;
}
