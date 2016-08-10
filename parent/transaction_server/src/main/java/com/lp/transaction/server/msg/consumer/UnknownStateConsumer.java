package com.lp.transaction.server.msg.consumer;

import com.lp.transaction.server.entity.TransactionRecordEntity;
import com.lp.transaction.server.enums.TopicInfo;
import com.lp.transaction.server.service.TransactionStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("unknownStateConsumer")
public class UnknownStateConsumer extends RocketMqConsumer {

	@Autowired
	private TransactionStateService stateService;

	public UnknownStateConsumer() {
		super(TopicInfo.UnkownTopic, 32, 1, 3);
		super.consumerName = "unknownStateConsumer";
	}

	@Override
	public void processMessage(TransactionRecordEntity record) {
		stateService.handleUnknownState(record);
	}
}
