package com.johnfnash.learn.redis.counter.service;

import com.johnfnash.learn.redis.counter.redis.util.StringRedisStringService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.concurrent.*;

@Slf4j
@Service
public class CounterService {

    public static final String COUNTER_KEY_PREFIX = "counter:";

    private BlockingQueue<String> queue;
    private ExecutorService fixedExecutor;

    private volatile boolean inited = false;
    private volatile boolean running = false;
    private final int QUEUE_CAPACITY = 100000;

    @Autowired
    private StringRedisStringService stringRedisStringService;

    @PostConstruct
    public void launch() {
        if (inited) {
            return;
        }
        inited = true;
        running = true;

        queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        fixedExecutor = Executors.newFixedThreadPool(1);
        fixedExecutor.execute(new CounterQueueHandler());
    }

    @PreDestroy
    private void shutdown() {
        running = false;
        inited = false;
        fixedExecutor.shutdown();
    }

    /**
     * 添加计数器
     * @param key
     */
    public void add(String key) {
        queue.add(COUNTER_KEY_PREFIX + key);
    }

    /**
     * 获取计数器
     * @param key
     * @return
     */
    public Integer getKeyCounter(String key) {
        return Integer.valueOf(stringRedisStringService.get(COUNTER_KEY_PREFIX + key));
    }

    /**
     * 计数器计数队列
     */
    private class CounterQueueHandler implements Runnable {

        @Override
        public void run() {
            String key;
            while (running) {
                try {
                    key = queue.take();
                    addOne(key, Duration.ofDays(1));
                } catch (Exception e) {
                    log.error("{}", e);
                    sleep(5);
                }
            }
        }
    }

    private void sleep(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    /**
     * 计数器队列消费者
     */
    private Long addOne(String key, Duration duration) {
        Long result = 1L;
        try {
            stringRedisStringService.setIfAbsentWithExpire(key, "0", duration);
            result = stringRedisStringService.increment(key);
            //解决并发问题，否则会导致计数器永不清空
            //如果incr的结果为1，有两个结果，第一种：先进行set操作，此时有过期时间。
            // 第二种：直接执行incr操作，此时的redisKey没有过期时间。所以需要补偿处理
            if (result == 1) {
                stringRedisStringService.expire(key, duration);
            }

            // 检查是否有过期时间，对异常没有设置过期时间的key补偿
            if (stringRedisStringService.getExpire(key) == -1) {
                stringRedisStringService.expire(key, duration);
            }
        } catch (Exception e) {
            log.error("{}", e);
            //丢到重试队列中，一直重试
            queue.offer(key);
        }
        return result;
    }

}
