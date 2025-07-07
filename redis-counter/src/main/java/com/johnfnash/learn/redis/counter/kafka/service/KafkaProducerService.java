package com.johnfnash.learn.redis.counter.kafka.service;

import com.johnfnash.learn.redis.counter.kafka.vo.KafkaDTOInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * kafka数据发送
 */
@Slf4j
@Component
public class KafkaProducerService {

	@Autowired
	private KafkaTemplate<String, KafkaDTOInterface> kafkaTemplate;
	@Autowired
	private KafkaTemplate<String, String> stringKafkaTemplate;

	public void sendMessageAsync(String topic, KafkaDTOInterface message) {
		// 异步发送消息
		kafkaTemplate.send(topic, message).addCallback(
				new ListenableFutureCallback<SendResult<String, KafkaDTOInterface>>() {
					@Override
					public void onSuccess(SendResult<String, KafkaDTOInterface> result) {
						log.info("✅ Message sent to partition: " + result.getRecordMetadata().partition());
						log.info("✅ Offset: " + result.getRecordMetadata().offset());
					}

					@Override
					public void onFailure(Throwable ex) {
						log.error("❌ Failed to send message: " + ex.getMessage());
					}
				}
		);
//		try {
//			future.get();
//        } catch (InterruptedException | ExecutionException e) {
//			log.error("kafka send message error", e);
//            throw new RuntimeException(e);
//        }
    }

	/**
	 * 异步发送消息
	 * @param message
	 */
	public void sendMessageAsync(String message) {
		stringKafkaTemplate.send("my-topic", message).addCallback(
				success -> {
					if (success != null) {
						log.info("Message sent successfully: " + message);
					}
				},
				failure -> {
					log.info("Failed to send message: " + message);
				}
		);
	}

}
