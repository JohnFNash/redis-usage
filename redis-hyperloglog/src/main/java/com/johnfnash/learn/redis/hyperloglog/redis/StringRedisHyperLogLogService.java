package com.johnfnash.learn.redis.hyperloglog.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;

/**
 * Redis-hyper-log-log 结构操作封装
 */
@Slf4j
@Component
public class StringRedisHyperLogLogService {

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
	public Long pfAdd(String key, String... value) {
		return getOpsForHLL().add(key, value);
	}

	private HyperLogLogOperations<String, String> getOpsForHLL() {
		return redisTemplate.opsForHyperLogLog();
	}

	/**
	 * 获取set的size
	 * 
	 * @param keys
	 * @return 当使用 pipeline / transaction 时，为0
	 */
	public long pfCount(String... keys) {
		Long size = getOpsForHLL().size(keys);
		return size == null ? 0 : size;
	}

	/**
	 * 返回多个key并集的基数
	 *
	 * @param destKey 目标key
	 * @param sourceKeys 源key
	 * @return
	 */
	public Long pfMerge(String destKey, String... sourceKeys) {
		return getOpsForHLL().union(destKey, sourceKeys);
	}

	/**
	 * 删除key
	 * @param key
	 */
	public void delete(String key) {
		getOpsForHLL().delete(key);
	}

	/**
	 * 网站UV统计示例
	 */
	public void trackUniqueVisitor(String date, String userId) {
		String key = "uv:" + date;
		pfAdd(key, userId);

		// 设置过期时间
		redisTemplate.expire(key, Duration.ofDays(7));
	}

	/**
	 * 获取指定日期的UV
	 */
	public Long getUniqueVisitors(String date) {
		return pfCount("uv:" + date);
	}

	/**
	 * 获取多日期UV合并统计
	 */
	public Long getUniqueVisitorsRange(String... dates) {
		String[] keys = Arrays.stream(dates)
				.map(date -> "uv:" + date)
				.toArray(String[]::new);
		return pfCount(keys);
	}

}
