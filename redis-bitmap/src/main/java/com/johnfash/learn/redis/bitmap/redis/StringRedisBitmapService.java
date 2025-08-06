package com.johnfash.learn.redis.bitmap.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Redis-bitmap 结构操作封装
 */
@Slf4j
@Component
public class StringRedisBitmapService {

	private static final int MAX_BIT_IN_ONE_BITMAP = 10000;

	@Resource(name = "redisRawTemplate")
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 设置位
	 *
	 * @param key
	 * @param offset
	 * @param value
	 * @return
	 */
	public Boolean setBit(String key, long offset, boolean value) {
		return getOpsForValue().setBit(key, offset, value);
	}

	private ValueOperations<String, String> getOpsForValue() {
		return redisTemplate.opsForValue();
	}

	/**
	 * 获取位
	 * @param key
	 * @param offset
	 * @return
	 */
	public Boolean getBit(String key, long offset) {
		return getOpsForValue().getBit(key, offset);
	}

	public List<Long> bitField(String key, BitFieldSubCommands commands) {
		return getOpsForValue().bitField(key, commands);
	}

	/**
	 * 获取位图所有1的个数
	 * @param key
	 * @return
	 */
	public Long bitCount(String key) {
		try {
			return redisTemplate.execute((RedisCallback<Long>) connection -> {
				Long result = connection.bitCount(key.getBytes());
				log.debug("bitCount for key '{}' returned: {}", key, result);
				return result;
			});
		} catch (Exception e) {
			log.error("Error executing bitCount for key '{}'", key, e);
			return null;
		}
	}

	/**
	 * 获取位图指定区间的1的个数
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Long bitCount(String key, long start, long end) {
		try {
			return redisTemplate.execute((RedisCallback<Long>) connection -> {
				Long result = connection.bitCount(key.getBytes(), start, end);
				log.debug("bitCount for key '{}' in range [{}, {}] returned: {}", key, start, end, result);
				return result;
			});
		} catch (Exception e) {
			log.error("Error executing bitCount for key '{}' in range [{}, {}]", key, start, end, e);
			return null;
		}
	}

	/**
	 * 获取位图指定位置的值
	 * @param key
	 * @param value
	 * @return
	 */
	public long bitPos(String key, boolean value) {
		return redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitPos(key.getBytes(), value));
	}

	/**
	 * 位运算-与
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long bitOpAnd(String destKey, String... keys) {
		byte[][] byteArrs = new byte[keys.length][];
		Arrays.asList(keys).stream().map(key -> key.getBytes()).collect(Collectors.toList()).toArray(byteArrs);
		return redisTemplate.execute((RedisCallback<Long>) connection ->
				connection.bitOp(RedisStringCommands.BitOperation.AND, destKey.getBytes(), byteArrs));
	}

	/**
	 * 位运算-或
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long bitOpOr(String destKey, String... keys) {
		byte[][] byteArrs = new byte[keys.length][];
		Arrays.asList(keys).stream().map(key -> key.getBytes()).collect(Collectors.toList()).toArray(byteArrs);
		return redisTemplate.execute((RedisCallback<Long>) connection ->
				connection.bitOp(RedisStringCommands.BitOperation.OR, destKey.getBytes(), byteArrs));
	}

	/**
	 * 位运算-异或
	 * @param destKey
	 * @param keys
	 * @return
	 */
	public long bitOpXor(String destKey, String... keys) {
		byte[][] byteArrs = new byte[keys.length][];
		Arrays.asList(keys).stream().map(key -> key.getBytes()).collect(Collectors.toList()).toArray(byteArrs);
		return redisTemplate.execute((RedisCallback<Long>) connection ->
				connection.bitOp(RedisStringCommands.BitOperation.XOR, destKey.getBytes(), byteArrs));
	}

	/**
	 * 位运算-取反
	 * @param destKey
	 * @param keys 源key，只接收一个key，多余的会被忽略
	 * @return
	 */
	public long bitOpNot(String destKey, String... keys) {
		byte[][] byteArrs = new byte[1][];
		Arrays.asList(keys[0]).stream().map(key -> key.getBytes()).collect(Collectors.toList()).toArray(byteArrs);
		return redisTemplate.execute((RedisCallback<Long>) connection ->
				connection.bitOp(RedisStringCommands.BitOperation.NOT, destKey.getBytes(), byteArrs));
	}

	/**
	 * 设置bit（自动进行分片）
	 * @param keyPrefix
	 * @param bizId
	 * @param value
	 * @return
	 */
	public Boolean setWithShard(String keyPrefix, long bizId, boolean value) {
		String key = getKey(keyPrefix, bizId);
		long offset = getOffset(bizId);
		return getOpsForValue().setBit(key, offset, value);
	}

	/**
	 * 获取bit（自动进行分片）
	 * @param prefixKey
	 * @param bizId
	 * @return
	 */
	public Boolean getWithShard(String prefixKey, long bizId) {
		String key = getKey(prefixKey, bizId);
		long offset = getOffset(bizId);
		return getOpsForValue().getBit(key, offset);
	}

	/**
	 * 获取位图所有1的个数（自动进行分片）。将各个分片进行求和
	 * @param keyPrefix
	 * @return
	 */
	public long bitCountWithShard(String keyPrefix) {
		List<String> keyList = scan(keyPrefix);
		return sumBitCount(keyList);
	}

	public long sumBitCount(List<String> keyList) {
		List<Object> resultList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (String key : keyList) {
				connection.bitCount(key.getBytes());
			}
			return null;
		});
		List<Long> collect = resultList.stream().filter(r -> r != null && r instanceof Long).map(r -> (Long) r).collect(Collectors.toList());
		return collect.stream().mapToLong(Long::longValue).sum();
	}

	/**
	 * 获取所有key
	 * @param key
	 * @return
	 */
	public List<String> scan(String key) {
		List<String> keyList = new ArrayList<>();
		ScanOptions.ScanOptionsBuilder builder = ScanOptions.scanOptions().match(key + "*");
		Cursor<String> cursor = redisTemplate.scan(builder.build());
		cursor.forEachRemaining(keyList::add);
		return keyList;
	}

	/**
	 * 位运算-或（按key前缀进行搜索）
	 * @param destKey
	 * @param keyPrefix
	 * @param delDestKey 或计算完成之后是否删除源key
	 * @return
	 */
	public long bitOpOrWithScan(String destKey, String keyPrefix, boolean delDestKey) {
		redisTemplate.delete(destKey);

		List<String> keys = scan(keyPrefix);
		byte[][] byteArrs = new byte[keys.size()][];
		keys.stream().map(key -> key.getBytes()).collect(Collectors.toList()).toArray(byteArrs);
		Long execute = redisTemplate.execute((RedisCallback<Long>) connection ->
				connection.bitOp(RedisStringCommands.BitOperation.OR, destKey.getBytes(), byteArrs));

		if (delDestKey) {
			redisTemplate.delete(keys);
		}

		return execute;
	}

	/**
	 * 获取key（自动进行分片）
	 * @param prefixKey
	 * @param bizId
	 * @return
	 */
	private String getKey(String prefixKey, long bizId) {
		int group = (int) (bizId / MAX_BIT_IN_ONE_BITMAP);
		return prefixKey + group;
	}

	/**
	 * 获取offset（自动进行分片）
	 * @param bizId
	 * @return
	 */
	private long getOffset(long bizId) {
		return bizId % MAX_BIT_IN_ONE_BITMAP;
	}

	public Boolean del(String key) {
		return redisTemplate.delete(key);
	}

}
