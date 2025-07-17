package com.johnfnash.learn.redis.shoppingcart.service;

import com.johnfnash.learn.redis.shoppingcart.entity.ShoppingCart;
import com.johnfnash.learn.redis.shoppingcart.vo.ShoppingCartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private static final String CART_PREFIX = "cart:";

    private static final long REDIS_KEY_EXPIRATION = 3600L;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, ShoppingCartVo> getOpsForHash() {
        return redisTemplate.opsForHash();
    }

    public void addShoppingCart(ShoppingCart shoppingCart) {
        String key = getCartKey(shoppingCart.getUserId());
        getOpsForHash().put(key, String.valueOf(shoppingCart.getProductId()), new ShoppingCartVo(shoppingCart));
        redisTemplate.expire(key, REDIS_KEY_EXPIRATION, TimeUnit.SECONDS);
    }

    public void updateCart(long userId, long productId, int quantity, double price) {
        String key = getCartKey(userId);
        ShoppingCartVo cart = getOpsForHash().get(key, String.valueOf(productId));
        cart.setQuantity(quantity);
        cart.setPrice(price);
        getOpsForHash().put(key, String.valueOf(productId), cart);
        redisTemplate.expire(key, REDIS_KEY_EXPIRATION, TimeUnit.SECONDS);
    }

    // 从购物车中删除商品
    public void removeItemFromCart(long userId, long productId) {
        getOpsForHash().delete(getCartKey(userId), String.valueOf(productId));
    }

    // 获取购物车所有商品
    public Map<String, ShoppingCartVo> getCart(long userId) {
        return getOpsForHash().entries(getCartKey(userId));
    }

    private static String getCartKey(long userId) {
        return CART_PREFIX + userId;
    }

    // 清空购物车
    public void clearCart(long userId) {
        redisTemplate.delete(getCartKey(userId));
    }

}
