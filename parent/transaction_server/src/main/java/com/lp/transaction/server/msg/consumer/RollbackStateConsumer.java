package com.lp.transaction.server.msg.consumer;

import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TopicInfo;
import com.lp.transaction.server.service.TransactionStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by 123 on 2016/8/2.
 */
@Component("rollbackStateConsumer")
public class RollbackStateConsumer extends RocketMqConsumer {
    @Autowired
    private TransactionStateService stateService;

    protected RollbackStateConsumer() {
        super(TopicInfo.RollbackTopic);
        super.consumerName = "rollbackStateConsumer";
    }

    @Override
    public void processMessage(TransactionRecordEntity record) {
        stateService.handleRollbackState(record);
    }
}
