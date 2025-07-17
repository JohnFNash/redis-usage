package com.johnfnash.learn.redis.set.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Redis-zset 结构操作封装
 */
@Slf4j
@Component
public class StringRedisSetService {

	@Resource(name = "redisRawTemplate")
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 添加元素
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public Long add(String key, String value) {
		return getOpsForSet().add(key, value);
	}

	/**
	 * 判断元素是否存在
	 * @param key
	 * @param value
	 * @return
	 */
	public Boolean exists(String key, String value) {
		return getOpsForSet().isMember(key, value);
	}

	private SetOperations<String, String> getOpsForSet() {
		return redisTemplate.opsForSet();
	}

	/**
	 * 获取set的size
	 * 
	 * @param key
	 * @return 当使用 pipeline / transaction 时，为0
	 */
	public long size(String key) {
		Long size = getOpsForSet().size(key);
		return size == null ? 0 : size;
	}

	/**
	 * 移除元素
	 * @param key
	 * @return
	 */
	public void remove(String key, String value) {
		getOpsForSet().remove(key, value);
	}

	public String pop(String key) {
		return getOpsForSet().pop(key);
	}

	public List<String> pop(String key, long count) {
		return getOpsForSet().pop(key, count);
	}

	public Set<String> getAll(String key) {
		return getOpsForSet().members(key);
	}

	/**
	 * 交集
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Set<String> intersect(String key1, String key2) {
		return getOpsForSet().intersect(key1, key2);
	}

	/**
	 * 并集
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Set<String> union(String key1, String key2) {
		return getOpsForSet().union(key1, key2);
	}

	/**
	 * 差集
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Set<String> difference(String key1, String key2) {
		return getOpsForSet().difference(key1, key2);
	}

}
