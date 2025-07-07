package com.johnfnash.learn.redis.message.controller;

import com.johnfnash.learn.redis.message.service.MessageService;
import com.johnfnash.learn.redis.message.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("getValidateCode")
    public String getValidateCode(@RequestParam("phoneNum") String phoneNum,
                                  HttpServletRequest request){
        return messageService.getPhoneCode(phoneNum, CommonUtils.getIpAddress(request));
    }

    /**
     用户点击确定登录按钮，会将前台输入的手机号、验证码发送到后端
     后端接受以后会和 Redis中的key进行比较
     如果相等 验证码比较成功，登录成功
     如果失败 验证码输入错误
     @param code
     @param phoneNum
     @return
     */
    @GetMapping("validateCode")
    public String validateCode(@RequestParam("code") String code,
                               @RequestParam("phoneNum") String phoneNum) {
        return messageService.validatePhoneCode(phoneNum, code);
    }

}
