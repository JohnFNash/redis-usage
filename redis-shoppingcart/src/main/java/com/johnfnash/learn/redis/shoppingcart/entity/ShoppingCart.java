package com.johnfnash.learn.redis.shoppingcart.entity;

import java.util.Date;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * (ShoppingCart)表实体类
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@TableName("shopping_cart")
public class ShoppingCart implements Serializable{
    private static final long serialVersionUID = -55939140123911988L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private Long productId;

    private Integer quantity;

    private Double price;

    private Date createTime;

    private Date updateTime;

    public ShoppingCart(long userId, long productId, int quantity, double price) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
}

