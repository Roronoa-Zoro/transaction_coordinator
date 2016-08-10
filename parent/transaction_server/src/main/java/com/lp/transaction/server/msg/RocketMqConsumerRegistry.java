package com.lp.transaction.server.msg;

import java.util.List;
import javax.annotation.PostConstruct;

import com.google.common.collect.ImmutableList;
import com.lp.transaction.server.msg.consumer.RocketMqConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by zhanghaolun on 16/7/9.
 */
@Slf4j
public class RocketMqConsumerRegistry {

    private final String namesrvAddr;
    private final String consumerGroupNamePrefix;

    public RocketMqConsumerRegistry(
            final String namesrvAddr,
            final String consumerGroupNamePrefix
    ) {
        this.namesrvAddr = namesrvAddr;
        this.consumerGroupNamePrefix = consumerGroupNamePrefix;
    }

    @Autowired
    @Setter
    private List<RocketMqConsumer> consumers = ImmutableList.of();

    @PostConstruct
    public void registerConsumers() {
        for (final RocketMqConsumer consumer : this.consumers) {
            this.registerConsumer(consumer);
        }
    }

    public void registerConsumer(final RocketMqConsumer consumer) {
        try {
            final DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer(this.consumerGroupNamePrefix + consumer.messageType.getTag());
            mqPushConsumer.setNamesrvAddr(this.namesrvAddr);
            //mqPushConsumer.setInstanceName(mqPushConsumer.getConsumerGroup());
            mqPushConsumer.subscribe(consumer.messageType.getTopic(), consumer.messageType.getTag());
            if (consumer.pullBatchSize > 0) {
                mqPushConsumer.setPullBatchSize(consumer.pullBatchSize);
            }
            if (consumer.minThreadCount > 0) {
                mqPushConsumer.setConsumeThreadMin(consumer.minThreadCount);
            }
            if (consumer.maxThreadCount > 0) {
                mqPushConsumer.setConsumeThreadMax(consumer.maxThreadCount);
            }

            mqPushConsumer.registerMessageListener(consumer.subscribeMessageListenerConcurrently());

            mqPushConsumer.start(); // Consumer对象在使用之前必须要调用start初始化，初始化一次即可
            log.info("Consumer:{},group:{}  is running。", consumer.consumerName, mqPushConsumer.getConsumerGroup());
        } catch (final Exception e) {
            log.error("So sorry!!!Occur an exception When Consume a message。", e);
            throw new RuntimeException(e);
        }
    }
}
