package com.lp.transaction.server.msg.consumer;


import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TopicInfo;
import com.lp.transaction.server.msg.converter.DefaultMessageConverter;
import com.lp.transaction.server.msg.converter.MessageConverter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;

import java.util.List;

@Slf4j
public abstract class RocketMqConsumer implements InitializingBean, DisposableBean {

    public String consumerName;
    @Setter
    private DefaultMQPushConsumer consumer;

    public final TopicInfo messageType;
    public final Integer pullBatchSize;
    public final Integer minThreadCount;
    public final Integer maxThreadCount;
    public final MessageConverter<TransactionRecordEntity> converter;

    protected RocketMqConsumer(final TopicInfo messageType) {
        this(messageType, 1, 0, 0, new DefaultMessageConverter<>(TransactionRecordEntity.class));
    }

    protected RocketMqConsumer(
            final TopicInfo messageType,
            final Integer pullBatchSize, // 幂等接口可以设置每次获取多个消息
            final Integer minThread,
            final Integer maxThread
    ) {
        this(messageType, pullBatchSize, minThread, maxThread, new DefaultMessageConverter<>(TransactionRecordEntity.class));
    }

    protected RocketMqConsumer(
            final TopicInfo messageType,
            final Integer pullBatchSize, // 幂等接口可以设置每次获取多个消息
            final Integer minThread,
            final Integer maxThread,
            MessageConverter<TransactionRecordEntity> converter
    ) {
        this.messageType = messageType;
        this.pullBatchSize = pullBatchSize;
        this.minThreadCount = minThread;
        this.maxThreadCount = maxThread;
        this.converter = converter;
    }

    public MessageListenerConcurrently subscribeMessageListenerConcurrently(){
        return (List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
            try {
                for(MessageExt msg : msgs) {
                    log.info("当前所消费的消息内容:{}",msg);
                    TransactionRecordEntity record = converter.convertFromByte(msg.getBody());
                    processMessage(record);
                }
            } catch (Exception e) {
                log.error("处理事务消息异常", e);
//                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
    }

    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    public abstract void processMessage(TransactionRecordEntity record);

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
