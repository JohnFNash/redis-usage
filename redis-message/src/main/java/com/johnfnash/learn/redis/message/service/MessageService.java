package com.johnfnash.learn.redis.message.service;

import com.johnfnash.learn.redis.message.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class MessageService {

    public static final String PHONE_CODE_KEY_PREFIX = "phone:code:";
    @Autowired
    @Qualifier("redisRawTemplate")
    private RedisTemplate redisRawTemplate;

    /**
     * 生成随机6位验证码（调用手机号api短信接口）
     */
     public int phoneCode(){
         // 创建一个随机数生成器。不使用 Random，因为 Random 不安全
        SecureRandom secureRandom = new SecureRandom();
        // 生成 100000 到 999999 之间的随机数（包含）
        return 100000 + secureRandom.nextInt(900000);
     }

     /** 根据用户的手机号生成redis中的key phone:code:手机号
     * 判断key是否存在，如果key不存在对key进行赋值，设置过期时间为60m
     * 如果key存在，提示用户：验证码以发送请注意查收短信
     * @param phoneNum
     * @return
      */
    public String getPhoneCode(String phoneNum, String ip) {
        if (CommonUtils.isPhoneInvalid(phoneNum)) {
            return "手机号格式错误";
        }

        String key = PHONE_CODE_KEY_PREFIX + phoneNum;
        //判断key是否存在，存在则短信已经发送
        if (redisRawTemplate.hasKey(key)) {
            return "验证码已发送请注意查收短信";
        }

        // 检查短信是否发送频繁
        if (!checkMessageLimit(ip)) {
            return "短信发送太频繁，请稍后再试";
        }

        // 短信未发送，则生成短信验证码并发送短信
        int code = phoneCode();
        redisRawTemplate.opsForValue().set(key, code, Duration.ofMinutes(1));

        // 发送短信
        // 这里调用短信接口发送短信

        return "验证码发送成功";
    }

    /**
     * 验证手机验证码
     * @param phoneNum
     * @param code
     * @return
     */
    public String validatePhoneCode(String phoneNum, String code) {
        if (CommonUtils.isPhoneInvalid(phoneNum) || code == null || code.length() != 6) {
            return "手机号或者验证码错误";
        }

        String key = PHONE_CODE_KEY_PREFIX + phoneNum;
        //判断验证码是否正确
        if (!code.equals(String.valueOf(redisRawTemplate.opsForValue().get(key)))) {
            return "验证码不正确";
        }

        //验证码正确，删除key
        redisRawTemplate.delete(key);

        // 执行登录业务逻辑
        // 后续登录逻辑省略
        System.out.println("验证成功，执行登录逻辑");
        return "登录成功！";
    }

    /**
     * 防止短信轰炸逻辑
     * 1、根据用户IP生成Redis Key：protectCode:具体ip
     * 2、每次获取验证码，判断Key是否存在
     *      如果不存在：执行set操作，并设置过期时间5分钟
     *      如果存在：直接执行incr +1
     * 3、判断如果当前用户访问超过3次，生成Redis key：lookIp:具体ip，
     *  锁定此用户12小时无法获取验证码（设置过期时间为12小时）
     */
    private boolean checkMessageLimit(String ip) {
        //优化代码：用户访问进来以后，直接判断redis中是否有对应的lockip，如果有，直接返回
        //IP锁定12小时
        String lockIp = "lockIp:" + ip;
        if (redisRawTemplate.hasKey(lockIp)) {
            return false;
        }

        //保护Key
        String protectKey = "message:" + ip;
        //判断保护key是否存在
        if (!redisRawTemplate.hasKey(protectKey)) {
            redisRawTemplate.opsForValue().set(protectKey, 1, Duration.ofMinutes(5));
        } else {
            redisRawTemplate.opsForValue().increment(protectKey);
        }

        // 判断是否超过限制
        if (((Integer) redisRawTemplate.opsForValue().get(protectKey)) > 3) {
            redisRawTemplate.opsForValue().set(lockIp, 1, Duration.ofHours(12));
            return false;
        }

        return true;
    }

}
