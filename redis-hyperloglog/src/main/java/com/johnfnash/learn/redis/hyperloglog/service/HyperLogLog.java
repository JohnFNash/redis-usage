package com.johnfnash.learn.redis.hyperloglog.service;

public class HyperLogLog {

    // 桶数的对数
    private final int b;
    // 桶数 = 2^b
    private final int m;
    // 修正常数
    private final double alpha;
    // 桶数组
    private final int[] buckets;

    public HyperLogLog(int b) {
        this.b = b;
        this.m = 1 << b;
        this.alpha = getAlpha(m);
        this.buckets = new int[m];
    }

    public void add(String element) {
        // 1.计算hash值
        long hash = hash64(element);

        // 2. 提取桶号（前b位）
        int bucketIndex = (int) (hash >>> (64 - b));

        // 3. 计算剩余位的前导零个数
        long w = hash << b;
        int leadingZeros = Long.numberOfLeadingZeros(w) + 1;

        // 4. 更新桶中的最大值
        buckets[bucketIndex] = Math.max(buckets[bucketIndex], leadingZeros);
    }

    /**
     * 估算基数
     * @return
     */
    public long cardinality() {
        // 1. 计算调和平均数的倒数
        double sum = 0.0;
        for (int bucket : buckets) {
            sum += Math.pow(2, -bucket);
        }
        double estimate = alpha * m * m / sum;

        // 2. 小范围修正
        if (estimate <= 2.5 * m) {
            int zeros = 0;
            for (int bucket : buckets) {
                if (bucket == 0) {
                    zeros++;
                }
            }
            if (zeros != 0) {
                return Math.round(m * Math.log(m / (double) zeros));
            }
        }

        // 3. 大范围修正
        else if (estimate >= (1.0/30.0) * (1L << 32)) {
            return Math.round(-1 * (1L << 32) * Math.log(1 - estimate / (1L << 32)));
        }
        
        // 4. 中等范围直接返回估算值
        return Math.round(estimate);
    }

    /**
     * 获取修正常数
     * @param m
     * @return
     */
    private static double getAlpha(int m) {
        switch (m) {
            case 16:
                return 0.673;
            case 32:
                return 0.697;
            case 64:
                return 0.709;
            default:
                return 0.7213 / (1 + 1.079 / m);
        }
    }

    /**
     * 计算hash值。不能直接使用value.hashCode()，因为hashCode()的产生的哈希值分布不够随机，特别是在低位上缺乏良好的分布特性
     * @param value
     * @return
     */
    private long hash64(String value) {
        // 使用更好的哈希函数，MurmurHash风格的简单实现
        long hash = value.hashCode();
        hash ^= (hash >>> 33);
        hash *= 0xff51afd7ed558ccdL;
        hash ^= (hash >>> 33);
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= (hash >>> 33);
        return hash;
    }

}
