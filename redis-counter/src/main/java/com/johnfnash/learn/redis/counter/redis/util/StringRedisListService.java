package com.johnfnash.learn.redis.counter.redis.util;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis-list 结构操作封装
 */
@Component
public class StringRedisListService {

	@Resource(name = "redisRawTemplate")
	private StringRedisTemplate redisTemplate;

	/**
	 * 弹出并删除队尾元素 阻塞队列，队列中没数据会一直阻塞
	 * 
	 * @param key
	 * @return
	 */
	public String popString(String key) {
		return getOpsForList().rightPop(key);
	}

	private ListOperations<String, String> getOpsForList() {
		return redisTemplate.opsForList();
	}

	/**
	 * 弹出并删除队尾元素 阻塞队列，队列中没数据会一直阻塞，直到超时
	 * 
	 * @param key
	 * @param timeoutInMs 超时毫秒
	 * @return
	 */
	public String popString(String key, long timeoutInMs) {
		return (String) getOpsForList().rightPop(key, timeoutInMs, TimeUnit.MILLISECONDS);
	}

	/**
	 * 插入队列
	 * 
	 * @param key
	 * @param value
	 */
	public void push(String key, String value) {
		getOpsForList().leftPush(key, value);
	}

	public void push(String key, Collection<String> values) {
		getOpsForList().leftPushAll(key, values);
	}

	public void push(String key, String... values) {
		getOpsForList().leftPushAll(key, values);
	}

	/**
	 * 获取list的size
	 * 
	 * @param key
	 * @return 当使用 pipeline / transaction 时，为0
	 */
	public long size(String key) {
		Long size = getOpsForList().size(key);
		return size == null ? 0 : size;
	}

}
