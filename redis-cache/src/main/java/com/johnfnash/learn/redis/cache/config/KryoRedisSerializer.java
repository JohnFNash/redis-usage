package com.johnfnash.learn.redis.cache.config;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoRedisSerializer<T> implements RedisSerializer<T> {

/**
    * 由于 Kryo 不是线程安全的。每个线程都应该有自己的 Kryo，Input 或 Output 实例。
    * 所以，使用 ThreadLocal 存放 Kryo 对象
    * 这样减少了每次使用都实例化一次 Kryo 的开销又可以保证其线程安全
    */
  private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
    Kryo kryo = new Kryo();
      // 设置循环引用
    kryo.setReferences(true);
      // 设置序列化时对象是否需要设置对象类型
    kryo.setRegistrationRequired(false);
    return kryo;
   });

    @Override
    public byte[] serialize(T t) {
        if (t == null) {
            return new byte[0];
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            // 对象的 Class 信息一起序列化
            kryo.writeClassAndObject(output, t);
            KRYO_THREAD_LOCAL.remove();
            output.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException("Error serializing object using Kryo", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Input input = new Input(bais)) {
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            // 通过存储在字节数组中的 Class 信息来确定反序列的类型
            Object object = kryo.readClassAndObject(input);
            KRYO_THREAD_LOCAL.remove();
            return (T) object;
        } catch (Exception e) {
            throw new SerializationException("Error deserializing object using Kryo", e);
        }
    }
}
