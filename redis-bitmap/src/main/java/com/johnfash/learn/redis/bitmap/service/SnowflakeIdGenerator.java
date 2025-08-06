package com.johnfash.learn.redis.bitmap.service;

public class SnowflakeIdGenerator {
    // 起始时间戳（纪元时间），可以根据需要调整
    private final long twepoch = 1735689600000L; // 2025-01-01 00:00:00

    // 各部分位数
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long sequenceBits = 12L;
    // 各部分的最大值
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private final long sequenceMask = ~(-1L << sequenceBits);
    // 各部分的移位操作
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    // 机器标识
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    // 上一次生成ID的时间戳
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        return nextId(timeGen());
    }

    public synchronized long nextId(long timestamp) {
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列递增
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间戳
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的ID
        return (timestamp << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    protected long timeGen() {
        return System.currentTimeMillis() - twepoch;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    public Long[] analyzeId(long id) {
        long timestamp = (id >> timestampLeftShift) + twepoch;
        long datacenterId = (id >> datacenterIdShift) & maxDatacenterId;
        long workerId = (id >> workerIdShift) & maxWorkerId;
        long sequence = id & sequenceMask;
        return new Long[]{timestamp, datacenterId, workerId, sequence};
    }

    public static void main(String[] args) throws InterruptedException {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
        long nextId = idGenerator.nextId();
        System.out.println(idGenerator.nextId());
        Thread.sleep(10);
        System.out.println(idGenerator.nextId());
        Thread.sleep(10);
        System.out.println(idGenerator.nextId());
        Thread.sleep(10);
        System.out.println(idGenerator.nextId());
        Thread.sleep(10);
        System.out.println(idGenerator.nextId());
//        idGenerator.analyzeId(nextId);
    }

}