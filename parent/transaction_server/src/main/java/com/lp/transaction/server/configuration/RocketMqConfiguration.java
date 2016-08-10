package com.lp.transaction.server.configuration;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.lp.transaction.server.msg.RocketMqConsumerRegistry;
import com.lp.transaction.server.msg.sender.DefaultRocketMqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Created by 123 on 2016/8/2.
 */
@Configuration
public class RocketMqConfiguration {

    @Autowired
    private Environment environment;
    @Value("${rocketmq.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.producerGroup}")
    private String producerGroup;
    @Value("${rocketmq.consumerGroupPrefix}")
    private String consumerGroupPrefix;
    @Value("${rocketmq.enabled}")
    private boolean rocketmqEnabled;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultRocketMqProducer defaultRocketMqProducer() {
        final DefaultRocketMqProducer defaultRocketMqProducer = new DefaultRocketMqProducer();
        if (rocketmqEnabled) {
            final DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
            producer.setNamesrvAddr(namesrvAddr);
            defaultRocketMqProducer.setProducer(producer);
        }
        return defaultRocketMqProducer;
    }

    @Bean
//    @Lazy
    public RocketMqConsumerRegistry consumerRegistrar() {
        final RocketMqConsumerRegistry consumerRegistrar = new RocketMqConsumerRegistry(namesrvAddr, consumerGroupPrefix);
        return consumerRegistrar;
    }
}
