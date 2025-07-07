package com.johnfnash.learn.redis.counter.redis.util;

import com.johnfnash.learn.redis.counter.vo.RedisPipelineReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Redis-HashMap 结构操作封装
 */
@Slf4j
@Component
public class StringRedisHashService {

	@Resource(name = "redisRawTemplate")
	private StringRedisTemplate redisTemplate;

	/**
	 * 从HashMap中删除hashKeys
	 * 
	 * @param key
	 * @return
	 */
	public Long delete(String key, String... hashKeys) {
		return getOpsForHash().delete(key, hashKeys);
	}

	/**
	 * 删除整个hash
	 * @param key
	 * @return
	 */
	public Boolean deleteAll(String key) {
		return getOpsForHash().getOperations().delete(key);
	}
	
	/**
	 * hashMap中是否存在hashKey
	 * 
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public Boolean hasKey(String key, String hashKey) {
		return getOpsForHash().hasKey(key, hashKey);
	}

	/**
	 * 获取元素
	 * 
	 * @param key
	 * @param hashKey
	 * @return 不存在或者使用pipeline/trasaction 时为空
	 */
	public String get(String key, String hashKey) {
		return getOpsForHash().get(key, hashKey);
	}

	public Map<String, String> getAll(String key) {
		return getOpsForHash().entries(key);
	}

	/**
	 * 获取hashMap的大小
	 * 
	 * @param key
	 * @return 当使用 pipeline/trasaction 时，返回0
	 */
	public long size(String key) {
		Long size = getOpsForHash().size(key);
		return size == null ? 0 : size;
	}
	
	/**
	 * 自增数字，原子操作
	 * @param key
	 * @param hkey
	 * @param num 可以为负数
	 * @return
	 */
	public Long increment(String key, String hkey, long num) {
		return getOpsForHash().increment(key, hkey, num);
	}
	
	/**
	 * 自增1
	 * @param key
	 * @param hkey
	 * @return
	 */
	public Long incrementOne(String key, String hkey) {
		return getOpsForHash().increment(key, hkey, 1);
	}
	
	public void put(String key, String hkey, String value) {
		getOpsForHash().put(key, hkey, value);
	}
	
	public Boolean putIfAbsent(String key, String hkey, String value) {
		return getOpsForHash().putIfAbsent(key, hkey, value);
	}

	private HashOperations<String, String, String> getOpsForHash() {
		return redisTemplate.opsForHash();
	}

	/**
	 * 批量设置 Hash 字段
	 * @param reqList
	 */
	public void executeHashPipeline(List<RedisPipelineReq> reqList) {
		redisTemplate.executePipelined((RedisCallback<String>) connection -> {
			// 批量设置 Hash 字段
			for (RedisPipelineReq req : reqList) {
				getOpsForHash().put(req.getKey(), req.getHkey(), req.getValue());
			}
			return null;
		});
	}

	/**
	 * 扫描指定前缀的key，并用这些keuy去执行function
	 * @param keyPrefix
	 * @param getValueFunction
	 * @return
	 */
	public boolean hashScanBatch(String keyPrefix, Function<List<String>, Map<String, String>> getValueFunction,
								 Function<Map<String, String>, Void> processFunction) {
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
					try {
						Map<String, String> valueMap = getValueFunction.apply(keyList);
						keyList.clear();
						if (!CollectionUtils.isEmpty(valueMap)) {
							processFunction.apply(valueMap);
						}
					} catch (Exception e) {
						log.info("scan batch process error", e);
						result = false;
					}
				}
			}

			if (!keyList.isEmpty()) {
				try {
					Map<String, String> valueMap = getValueFunction.apply(keyList);
					keyList.clear();
					if (!CollectionUtils.isEmpty(valueMap)) {
						processFunction.apply(valueMap);
					}
				} catch (Exception e) {
					log.info("scan batch process error", e);
					result = false;
				}
			}
		} finally {
			// 关闭游标以释放资源
			cursor.close();
		}

		return result;
	}

}
