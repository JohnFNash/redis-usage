package com.johnfnash.learn.redis.roaringbitmap.service;

import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.longlong.Roaring64Bitmap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class RoaringBitmapService {

    @Autowired
    @Qualifier("redisByteTemplate")
    private RedisTemplate<String, byte[]> bitRedisTemplate;

    /**
     * 将RoaringBitmap64序列化为字节数组
     * @param bitmap
     * @param key
     * @throws IOException
     */
    public void saveRoaringBitmap64(Roaring64Bitmap bitmap, String key) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        // 这里调用 runOptimize来对比RunContainer和其他两个Container存储空间大小，如果使用RunContainer存储空间更佳则会进行转化
        bitmap.runOptimize();
        bitmap.serialize(dataOutputStream);
        byte[] serializedBitmap  = byteArrayOutputStream.toByteArray();

        // 使用RedisTemplate将序列化的RoaringBitmap存入Redis
        bitRedisTemplate.opsForValue().set(key, serializedBitmap);
    }

    /**
     * 从Redis中获取序列化的RoaringBitmap
     * @param key
     * @return
     * @throws IOException
     */
    public Roaring64Bitmap loadRoaringBitmap64(String key) throws IOException {
        byte[] serializedBitmap = bitRedisTemplate.opsForValue().get(key);
        // 如果存在则反序列化为RoaringBitmap
        if (serializedBitmap != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedBitmap);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
            Roaring64Bitmap bitmap = new Roaring64Bitmap();
            bitmap.deserialize(dataInputStream);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 将RoaringBitmap序列化为字节数组
     * @param bitmap
     * @param key
     * @throws IOException
     */
    public void saveRoaringBitmap(RoaringBitmap bitmap, String key) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        // 这里调用 runOptimize来对比RunContainer和其他两个Container存储空间大小，如果使用RunContainer存储空间更佳则会进行转化
        bitmap.runOptimize();
        bitmap.serialize(dataOutputStream);
        byte[] serializedBitmap  = byteArrayOutputStream.toByteArray();
        // 使用RedisTemplate将序列化的RoaringBitmap存入Redis
        bitRedisTemplate.opsForValue().set(key, serializedBitmap);
    }

    /**
     * 从Redis中获取序列化的RoaringBitmap
     * @param key
     * @return
     * @throws IOException
     */
    public RoaringBitmap loadRoaringBitmap(String key) throws IOException {
        byte[] serializedBitmap = bitRedisTemplate.opsForValue().get(key);
        // 如果存在则反序列化为RoaringBitmap
        if (serializedBitmap != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedBitmap);
            DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
            RoaringBitmap bitmap = new RoaringBitmap();
            bitmap.deserialize(dataInputStream);
            return bitmap;
        } else {
            return null;
        }
    }

}
