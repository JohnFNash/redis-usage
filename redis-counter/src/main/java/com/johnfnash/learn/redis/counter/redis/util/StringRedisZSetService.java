package com.johnfnash.learn.redis.counter.redis.util;

import com.johnfnash.learn.redis.counter.redis.vo.ZSetRangeBatchReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;

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
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean add(String key, String value) {
		return getOpsForZSet().add(key, value, System.currentTimeMillis());
	}

	private ZSetOperations<String, String> getOpsForZSet() {
		return redisTemplate.opsForZSet();
	}

	/**
	 * 弹出并删除队尾元素 阻塞队列，队列中没数据会一直阻塞，直到超时
	 *
	 * @param key
	 * @param value 超时毫秒
	 * @return
	 */
	public boolean addIfAbsent(String key, String value) {
		return getOpsForZSet().addIfAbsent(key, value, System.currentTimeMillis());
	}

	/**
	 * 获取zset的size
	 * 
	 * @param key
	 * @return 当使用 pipeline / transaction 时，为0
	 */
	public long size(String key) {
		Long size = getOpsForZSet().size(key);
		return size == null ? 0 : size;
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
	 * 计算分数在给定范围的元素数量
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Long count(String key, double min, double max) {
		return getOpsForZSet().count(key, min, max);
	}

	/**
	 * 获取分数在给定范围的元素
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<String> rangeByScore(String key, double min, double max, long offset, long count) {
		return getOpsForZSet().rangeByScore(key, min, max, offset, count);
	}

	public Set<String> rangeByScore(String key, double min, double max) {
		return getOpsForZSet().rangeByScore(key, min, max);
	}

	public Set<String> getAll(String key) {
		return getOpsForZSet().range(key, 0, -1);
	}

	/**
	 * 批量获取指定key的分数在给定曲建内的元素
	 * @param req
	 * @return
	 */
	public Map<String, Set<String>> scanZSet(ZSetRangeBatchReq req) {
		List<String> keyList = req.getKeyList();
		if (CollectionUtils.isEmpty(keyList)) {
			return Collections.emptyMap();
		}

		double min = req.getMin();
		double max = req.getMax();
		Map<String, Set<String>> updateArticleCollectionMap = new HashMap<>(keyList.size());
		redisTemplate.executePipelined((RedisCallback<String>) connection -> {
			// 批量设置 Hash 字段
			for (String key : keyList) {
				Set<String> Strings = rangeByScore(key, min, max);
				if (!CollectionUtils.isEmpty(Strings)) {
					updateArticleCollectionMap.put(key, Strings);
				}
			}
			return null;
		});
		return updateArticleCollectionMap;
	}

	/**
	 * 扫描指定前缀的key，并用这些key去执行获取zset给定分数范围内的值，并用这些值去执行processFunction
	 * @param keyPrefix
	 * @param getValueFunction
	 * @return
	 */
	public boolean zSetScanBatch(String keyPrefix, Function<ZSetRangeBatchReq, Map<String, Set<String>>> getValueFunction,
								 double min, double max, Function<Map<String, Set<String>>, Boolean> processFunction) {
		boolean result = true;

		ScanOptions options = ScanOptions.scanOptions().match(keyPrefix + "*").build();
		Cursor<String> cursor = redisTemplate.scan(options);
		try {
			// 迭代结果
			int count = 0;
			List<String> keyList = new ArrayList<>(200);
			while (cursor.hasNext()) {
				keyList.add(cursor.next());
				count++;
				if (count % 200 == 0) {
					Map<String, Set<String>> filteredValue = getValueFunction.apply(ZSetRangeBatchReq.build(keyList, min, max));
					keyList.clear();
					if (!CollectionUtils.isEmpty(filteredValue)) {
						Boolean success = processFunction.apply(filteredValue);
						if (!success) {
							log.info("scan batch process error");
							result = false;
							break;
						}
					}
				}
			}

			if (!keyList.isEmpty()) {
				Map<String, Set<String>> filteredValue = getValueFunction.apply(ZSetRangeBatchReq.build(keyList, min, max));
				keyList.clear();
				if (!CollectionUtils.isEmpty(filteredValue)) {
					Boolean success = processFunction.apply(filteredValue);
					if (!success) {
						log.info("scan batch process error");
						result = false;
					}
				}
			}
		} finally {
			// 关闭游标以释放资源
			cursor.close();
		}

		return result;
	}

	/**
	 * 扫描指定前缀的key，并用这些key去执行获取zset的值，并用这些值去执行processFunction
	 * @param keyPrefix
	 * @param getValueFunction
	 * @return
	 */
	public boolean zSetScanBatch(String keyPrefix, Function<List<String>, Map<String, Set<String>>> getValueFunction,
								 Function<Map<String, Set<String>>, Void> processFunction) {
		boolean result = true;

		ScanOptions options = ScanOptions.scanOptions().match(keyPrefix + "*").build();
		Cursor<String> cursor = redisTemplate.scan(options);
		try {
			// 迭代结果
			int count = 0;
			List<String> keyList = new ArrayList<>(100);
			while (cursor.hasNext()) {
				keyList.add(cursor.next());
				count++;
				if (count % 100 == 0) {
					Map<String, Set<String>> filteredValue = getValueFunction.apply(keyList);
					keyList.clear();
					if (!CollectionUtils.isEmpty(filteredValue)) {
						processFunction.apply(filteredValue);
					}
				}
			}

			if (!keyList.isEmpty()) {
				Map<String, Set<String>> filteredValue = getValueFunction.apply(keyList);
				keyList.clear();
				if (!CollectionUtils.isEmpty(filteredValue)) {
					processFunction.apply(filteredValue);
				}
			}
		} finally {
			// 关闭游标以释放资源
			cursor.close();
		}

		return result;
	}

}
