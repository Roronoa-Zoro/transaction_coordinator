package com.lp.transaction.server.msg.sender;

import com.lp.transaction.server.constants.TransactionConstant;
import com.lp.transaction.server.msg.MessageWraper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Slf4j
public class DefaultRocketMqProducer {
	/**
	 * 消息发送的appKey
	 */
	private String msgAppKey;
	/**
	 * mq生产者
	 */
	private DefaultMQProducer producer;

	public void start() throws MQClientException {
		if (this.producer != null) {
			this.producer.start();
			log.info("producer启动完毕。。。");
		} else {
			log.error("producer无法启动，可能是因为mq开关没有开启。开关参数：rocketmq.enabled。");
		}
	}

	public void shutdown() {
		if (this.producer != null) {
			this.producer.shutdown();
			log.info("producer已关闭。。。");
		} else {
			log.error("producer无法关闭，可能是已被其他线程开关或未开启。");
		}
	}

	/**
	 * 推送消息
	 *
	 * @param envelope
	 * @return
	 */
	public boolean send(final MessageWraper<?> envelope) {
		try {
			final byte[] msgBytes;
			if (envelope.getMsg().getClass().isAssignableFrom(String.class)) {
				log.info("消息内容:{}", envelope.getMsg());
				msgBytes = ((String) envelope.getMsg()).getBytes(TransactionConstant.CHARSET_UTF8);
			} else {
				String msg = JSON.toJSONString(envelope.getMsg());
				log.info("消息内容:{}", msg);
				msgBytes = msg.getBytes(TransactionConstant.CHARSET_UTF8);
			}

			final Message message = new Message(
					envelope.getType().getTopic(), // topic
					envelope.getType().getTag(), // tag
					envelope.getUuid(), // key
					msgBytes // body
			);
			final SendResult sr = this.producer.send(message);
			log.debug("消息推送结果:{}", JSON.toJSONString(sr));
			if (sr != null && sr.getSendStatus() == SendStatus.SEND_OK) {
				log.debug("消息推送成功");
				return true;
			}

			log.debug("消息推送失败");
			return false;

		} catch (Exception e) {
			log.error("消息推送失败了", e);
			return false;
		}
	}
}