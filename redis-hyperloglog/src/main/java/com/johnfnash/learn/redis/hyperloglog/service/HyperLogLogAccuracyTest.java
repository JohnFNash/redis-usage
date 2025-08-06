package com.johnfnash.learn.redis.hyperloglog.service;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
public class HyperLogLogAccuracyTest {

    public void testAccuracy() {
        HyperLogLog hll = new HyperLogLog(14); // 16384桶
        Set<String> actualSet = new HashSet<>();

        // 添加100万个随机元素
        Random random = new Random();
        for (int i = 0; i < 1000000; i++) {
            String element = String.valueOf(random.nextInt(800000));
            hll.add(element);
            actualSet.add(element);
        }

        long actualCardinality = actualSet.size();
        long estimatedCardinality = hll.cardinality();

        double errorRate = Math.abs(estimatedCardinality - actualCardinality)
                / (double) actualCardinality * 100;

        System.out.printf("实际基数: %d%n", actualCardinality);
        System.out.printf("估算基数: %d%n", estimatedCardinality);
        System.out.printf("误差率: %.2f%%%n", errorRate);
    }

}
