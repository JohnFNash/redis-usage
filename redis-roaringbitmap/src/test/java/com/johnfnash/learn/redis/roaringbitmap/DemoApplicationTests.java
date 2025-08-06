package com.johnfnash.learn.redis.roaringbitmap;

import com.johnfnash.learn.redis.roaringbitmap.service.RoaringBitmapService;
import com.johnfnash.learn.redis.roaringbitmap.service.SnowflakeIdGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.roaringbitmap.Container;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.art.ContainerIterator;
import org.roaringbitmap.longlong.HighLowContainer;
import org.roaringbitmap.longlong.LongConsumer;
import org.roaringbitmap.longlong.Roaring64Bitmap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=RedisRoaringBitmapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class DemoApplicationTests {

    @Autowired
    @Qualifier("redisByteTemplate")
    private RedisTemplate<String, byte[]> redisTemplate;
    @Autowired
    @Qualifier("redisLongTemplate")
    private RedisTemplate<String, Long> redisLongTemplate;
    @Autowired
    private RoaringBitmapService roaringBitmapService;
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;

    private static final String KEY = "bitmapSnowflakeIdKey";

    private static final long MAX_ITEM_IN_ONE_BITMAP = 100000;

    @Test
    public void test() {
        String key = "bitmapKey";
        RoaringBitmap rb = new RoaringBitmap();
        for(int i=0; i<10000; i++) {
            int num = new Random().nextInt(10000000);
            // 添加到roaringBitmap
            rb.add(num);
            // 同样添加到Bitmap，进行对比
            redisTemplate.opsForValue().setBit(key, num, true);
        }
        int total = rb.getSizeInBytes();
        System.out.println("RoaringBitmap 大小：" + total/1024 + "kb");
    }

    @Test
    public void memTest1Func() {
        Long startTime = System.currentTimeMillis();
        Long start = 1734856521692028928L;
        for (int i = 0; i < 1000000; i++) {
            redisLongTemplate.opsForSet().add("test1:memtest", start + i);
        }
        Long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) / 1000);
    }

    @Test
    public void memTest2Func() throws IOException, NoSuchFieldException, IllegalAccessException {
        Long startTime = System.currentTimeMillis();
        Roaring64Bitmap bitmap = new Roaring64Bitmap();
        Long start = 1734856521692028928L;
        for (int i = 0; i < 1000000; i++) {
            bitmap.add(start + i);
        }
        roaringBitmapService.saveRoaringBitmap64(bitmap, "test2:memtest");
        Long endTime = System.currentTimeMillis();
        System.out.println("压缩耗时：" + (endTime - startTime) / 1000);

        printContainerName(bitmap);
    }

    private static void printContainerName(Roaring64Bitmap bitmap) throws NoSuchFieldException, IllegalAccessException {
        // 尝试通过反射获取highLowContainer字段
        Field highLowContainerField = Roaring64Bitmap.class.getDeclaredField("highLowContainer");
        highLowContainerField.setAccessible(true);
        Object highLowContainer = highLowContainerField.get(bitmap);

        if (highLowContainer instanceof HighLowContainer) {
            // 假设highLowContainer是一个Map结构
            ContainerIterator containerIterator = ((HighLowContainer) highLowContainer).containerIterator();
            while (containerIterator.hasNext()) {
                Container container = containerIterator.next();
                System.out.println("Container: " + container.getContainerName());
            }
        }
    }

    @Test
    public void memTest3Func() throws IOException, NoSuchFieldException, IllegalAccessException {
        Long startTime = System.currentTimeMillis();
        Roaring64Bitmap bitmap = new Roaring64Bitmap();
        for (int i = 0; i < 1000000; i++) {
            bitmap.add(new Random().nextLong());
        }
        roaringBitmapService.saveRoaringBitmap64(bitmap, "test3:memtest");
        Long endTime = System.currentTimeMillis();
        System.out.println("压缩耗时：" + (endTime - startTime) / 1000);

        printContainerName(bitmap);
    }

    @Test
    public void memTest4Func() throws IOException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        Long startTime = System.currentTimeMillis();
        Roaring64Bitmap bitmap = new Roaring64Bitmap();
        for (int i = 0; i < 10000; i++) {
            long nextId = snowflakeIdGenerator.nextId();
            bitmap.add(nextId);
            // 同样添加到Bitmap，进行对比
            redisTemplate.opsForValue().setBit(getKey(nextId), getOffset(nextId), true);
            Thread.sleep(1);
        }
        roaringBitmapService.saveRoaringBitmap64(bitmap, "test4:memtest");
        Long endTime = System.currentTimeMillis();
        System.out.println("压缩耗时：" + (endTime - startTime) / 1000);

        printContainerName(bitmap);

        bitmap.forEach(new LongConsumer() {
            @Override
            public void accept(long l) {
                System.out.println(l);
            }
        });
    }

    /**
     * 获取key前缀
     * @param userId
     * @return
     */
    private static String getKey(long userId) {
        long shard = userId / MAX_ITEM_IN_ONE_BITMAP;
        return KEY + shard;
    }

    private static long getOffset(long userId) {
        return userId % MAX_ITEM_IN_ONE_BITMAP;
    }

}
