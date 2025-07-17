package com.johnfnash.learn.redis.shoppingcart.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.johnfnash.learn.redis.shoppingcart.vo.ShoppingCartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.johnfnash.learn.redis.shoppingcart.mapper.ShoppingCartMapper;
import com.johnfnash.learn.redis.shoppingcart.entity.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * (ShoppingCart)表服务实现类
 */
@Service("shoppingCartService")
public class ShoppingCartService extends ServiceImpl<ShoppingCartMapper, ShoppingCart> {

    @Autowired
    private CacheService cacheService;

    @Transactional
    public void addToCart(long userId, long productId, int quantity, double price) {
        // 1. 向数据库中插入购物车记录
        // 这里应该先查询数据库，如果存在则更新，不存在则插入。这里先简化
        ShoppingCart shoppingCart = new ShoppingCart(userId, productId, quantity, price);
        shoppingCart.setCreateTime(new Date());
        shoppingCart.setUpdateTime(new Date());
        baseMapper.insert(shoppingCart);

        // 2. 向Redis中插入购物车记录
        cacheService.addShoppingCart(shoppingCart);
    }

    @Transactional
    public void updateCart(long userId, long productId, int quantity, double price) {
        // 1. 更新数据库中的购物车记录
        baseMapper.updateCart(userId, productId, quantity, price, new Date());

        // 2. 更新Redis中的购物车记录
        cacheService.updateCart(userId, productId, quantity, price);
    }

    @Transactional
    public void deleteFromCart(long userId, long productId) {
        // 1. 删除数据库中的购物车记录
        baseMapper.deleteByUserIdAndProductId(userId, productId);

        // 2. 删除Redis中的购物车记录
        cacheService.removeItemFromCart(userId, productId);
    }

    // 清空购物车
    @Transactional
    public void clearCart(long userId) {
        // 1. 情况数据库中的购物车记录
        baseMapper.deleteByUserId(userId);

        // 2. 清空Redis中的购物车记录
        cacheService.clearCart(userId);
    }

    // 获取购物车
    @Transactional(readOnly = true)
    public List<ShoppingCartVo> getCart(long userId) {
        // 1. 从Redis中获取购物车记录
        Map<String, ShoppingCartVo> redisEntries = cacheService.getCart(userId);

        // 2. 如果Redis中不存在购物车记录，则从数据库中获取
        if (CollectionUtils.isEmpty(redisEntries)) {
            List<ShoppingCart> cartList = baseMapper.selectByUserId(userId);
            return cartList.stream().map(ShoppingCartVo::new).collect(Collectors.toList());
        } else {
            return redisEntries.entrySet().stream().map(entry -> {
                // 这里将产品ID设置进去，因为redis里没有存储产品ID，field是产品ID
                ShoppingCartVo cartVo = entry.getValue();
                cartVo.setProductId(Long.valueOf(entry.getKey()));
                return cartVo;
            }).collect(Collectors.toList());
        }
    }

}
