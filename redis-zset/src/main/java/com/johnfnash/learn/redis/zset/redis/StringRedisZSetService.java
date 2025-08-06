package com.johnfnash.learn.redis.zset.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Redis-zset 结构操作封装
 */
@Slf4j
@Component
public class StringRedisZSetService {

	@Resource(name = "redisRawTemplate")
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 添加元素
	 *
	 * @param key
	 * @param value
	 * @param score
	 * @return
	 */
	public Boolean add(String key, String value, double score) {
		return getOpsForZSet().add(key, value, score);
	}

	/**
	 * 批量添加元素
	 *
	 * @param key
	 * @param tuples
	 * @return
	 */
	public Long addBatch(String key, Set<ZSetOperations.TypedTuple<String>> tuples) {
		return getOpsForZSet().add(key, tuples);
	}

	private ZSetOperations<String, String> getOpsForZSet() {
		return redisTemplate.opsForZSet();
	}

	/**
	 * 获取set的size
	 * 
	 * @param key
	 * @return 当使用 pipeline / transaction 时，为0
	 */
	public long size(String key) {
		Long size = getOpsForZSet().size(key);
		return size == null ? 0 : size;
	}

	/**
	 * 获取指定分数区间的元素个数
	 *
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long countByScoreRange(String key, double min, double max) {
		return getOpsForZSet().count(key, min, max);
	}

	/**
	 * 移除元素
	 * @param key
	 * @return
	 */
	public void remove(String key, String value) {
		getOpsForZSet().remove(key, value);
	}

	/**
	 * 批量移除元素
	 * @param key
	 * @param values
	 */
	public void removeBatch(String key, Set<String> values) {
		getOpsForZSet().remove(key, values.toArray());
	}

	/**
	 * 移除区间内元素
	 * @param key
	 * @param start
	 * @param end
	 */
	public void removeRange(String key, long start, long end) {
		getOpsForZSet().removeRange(key, start, end);
	}

	/**
	 * 根据分数移除区间内元素
	 * @param key
	 * @param start
	 * @param end
	 */
	public void removeRangeByScore(String key, double start, double end) {
		getOpsForZSet().removeRangeByScore(key, start, end);
	}

	/**
	 * 获取给定元素的排名
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public Long rank(String key, String value) {
		return getOpsForZSet().rank(key, value);
	}

	/**
	 * 获取给定元素的排名（从大到小倒序排）
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public Long reverseRank(String key, String value) {
		return getOpsForZSet().reverseRank(key, value);
	}

	public ZSetOperations.TypedTuple<String> popMax(String key) {
		return getOpsForZSet().popMax(key);
	}

	public Set<ZSetOperations.TypedTuple<String>> popMax(String key, long count) {
		return getOpsForZSet().popMax(key, count);
	}

	public Set<String> getAllValues(String key) {
		return getOpsForZSet().range(key, 0, -1);
	}

	/**
	 * 获取指定区间内的元素
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> range(String key, long start, long end) {
		return getOpsForZSet().range(key, start, end);
	}

	/**
	 * 获取指定区间内的元素（从大到小）
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<String> reverseRange(String key, long start, long end) {
		return getOpsForZSet().reverseRange(key, start, end);
	}

	/**
	 * 获取分数在指定区间内的元素
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<String> rangeWithScores(String key, double min, double max) {
		return getOpsForZSet().rangeByScore(key, min, max);
	}

	public Set<ZSetOperations.TypedTuple<String>> getAllWithScores(String key) {
		return getOpsForZSet().rangeWithScores(key, 0, -1);
	}

	/**
	 * scan
	 * @param key
	 */
	public void scan(String key) {
		ScanOptions.ScanOptionsBuilder builder = ScanOptions.scanOptions().match("*");
		Cursor<ZSetOperations.TypedTuple<String>> cursor = getOpsForZSet().scan(key, builder.build());
		cursor.forEachRemaining(r -> System.out.println(r.getValue() + ":" + r.getScore()));
	}

	/**
	 * 增加指定元素的分数
	 * @param key
	 * @param value
	 * @return
	 */
	public Double incrementScore(String key, String value, double delta) {
		return getOpsForZSet().incrementScore(key, value, delta);
	}

	/**
	 * 获取元素的分数
	 * @param key
	 * @param value
	 * @return
	 */
	public Double getScore(String key, String value) {
		return getOpsForZSet().score(key, value);
	}

	/**
	 * 交集
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Set<String> intersect(String key1, String key2) {
		return getOpsForZSet().intersect(key1, key2);
	}

	/**
	 * 交集并保存
	 * @param key1
	 * @param key2
	 * @param destKey
	 */
	public void intersectAndStore(String key1, String key2, String destKey) {
		getOpsForZSet().intersectAndStore(key1, key2, destKey);
	}

	/**
	 * 并集
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Set<String> union(String key1, String key2) {
		return getOpsForZSet().union(key1, key2);
	}

	/**
	 * 并集并保存
	 * @param key1
	 * @param key2
	 * @param destKey
	 */
	public void unionAndStore(String key1, String key2, String destKey) {
		getOpsForZSet().unionAndStore(key1, key2, destKey);
	}

	/**
	 * 差集
	 * @param key1
	 * @param key2
	 * @return
	 */
	public Set<String> difference(String key1, String key2) {
		return getOpsForZSet().difference(key1, key2);
	}

	/**
	 * 差集并保存
	 * @param key1
	 * @param otherKeyList
	 * @param destKey
	 */
	public void differenceAndStore(String key1, List<String> otherKeyList, String destKey) {
		getOpsForZSet().differenceAndStore(key1, otherKeyList, destKey);
	}

}
