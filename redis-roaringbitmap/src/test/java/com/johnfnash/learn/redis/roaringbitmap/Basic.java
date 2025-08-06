package com.johnfnash.learn.redis.roaringbitmap;

import org.roaringbitmap.IntConsumer;
import org.roaringbitmap.RoaringBitmap;

public class Basic {

    public static void main(String[] args) {
        RoaringBitmap rr = RoaringBitmap.bitmapOf(1, 2, 3, 1000);
        System.out.println(rr.select(3));   //返回RBM中的第4个元素，索引从0开始
        System.out.println(rr.rank(2)); // 返回<=x的元素个数
        System.out.println(rr.contains(1000)); // 判断元素是否在RBM中。这里返回true
        System.out.println(rr.contains(7));    // 这里返回false

        RoaringBitmap rr2 = new RoaringBitmap();
        rr2.add(4000L, 4255L);
        RoaringBitmap rror = RoaringBitmap.and(rr, rr2); // new bitmap
        rror.or(rr); // 两个RBM进行or操作进行合并
        boolean equals = rror.equals(rr); // true
        if (!equals) {
            throw new RuntimeException("bug");
        }

        // 打印元素的个数
        long cardinality = rr.getLongCardinality();
        System.out.println("cardinality:" + cardinality);
        rr.forEach(new IntConsumer() {
            @Override
            public void accept(int i) {
                System.out.println(i);
            }
        });
    }

}
