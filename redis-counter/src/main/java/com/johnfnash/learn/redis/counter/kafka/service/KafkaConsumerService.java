package com.johnfnash.learn.redis.counter.kafka.service;

import com.alibaba.fastjson.JSONObject;
import com.johnfnash.learn.redis.counter.kafka.KafkaConfig;
import com.johnfnash.learn.redis.counter.kafka.vo.ArticleCollectVo;
import com.johnfnash.learn.redis.counter.kafka.vo.KafkaDTOInterface;
import com.johnfnash.learn.redis.counter.kafka.vo.UserFollowVo;
import com.johnfnash.learn.redis.counter.service.ArticleCollectionService;
import com.johnfnash.learn.redis.counter.service.UserFollowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * kafka消息接收
 */
@Slf4j
@Component
public class KafkaConsumerService {

	@Autowired
	private UserFollowService userFollowService;
	@Autowired
	private ArticleCollectionService articleCollectionService;

	@KafkaListener(topics = KafkaConfig.USER_FOLLOW_TOPIC, groupId = KafkaConfig.GROUP_ID, batch = "true")
	public void consumeUserFollowBatch(List<KafkaDTOInterface> messages, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		List<UserFollowVo> voList = new ArrayList<>(messages.size());
		for (KafkaDTOInterface message : messages) {
			log.info(KafkaConfig.GROUP_ID + " 消费了： Topic:" + topic + ", Message:" + message);
			voList.add((UserFollowVo) message);
		}
		userFollowService.saveUserFollowBatch(voList, true);
	}

	@KafkaListener(topics = KafkaConfig.ARTICLE_COLLECT_TOPIC, groupId = KafkaConfig.GROUP_ID)
	public void consumeArticleCollectBatch(List<KafkaDTOInterface> messages, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		List<ArticleCollectVo> voList = new ArrayList<>(messages.size());
		for (KafkaDTOInterface message : messages) {
			log.info(KafkaConfig.GROUP_ID + " 消费了： Topic:" + topic + ", Message:" + message);
			voList.add((ArticleCollectVo)message);
		}
		articleCollectionService.syncArticleCollectionToDB(voList);
	}

	@DltHandler
	public void handleDlq(ConsumerRecord<String, String> record) {
		// 处理死信队列中的消息
		System.out.println("DLQ Message: " + record.value());
	}

}
