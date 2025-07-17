package com.johnfnash.learn.redis.shoppingcart.vo;

import com.johnfnash.learn.redis.shoppingcart.entity.ShoppingCart;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (ShoppingCart)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
public class ShoppingCartVo implements Serializable{
    private static final long serialVersionUID = -55939140123911988L;

    private Integer quantity;

    private Double price;

    private Long productId;

    public ShoppingCartVo(ShoppingCart shoppingCart) {
        this.quantity = shoppingCart.getQuantity();
        this.price = shoppingCart.getPrice();
        this.productId = shoppingCart.getProductId();
    }
}

