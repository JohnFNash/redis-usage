package com.johnfnash.learn.redis.shoppingcart.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.johnfnash.learn.redis.shoppingcart.entity.ShoppingCart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * (ShoppingCart)表数据库访问层
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

    @Select("update shopping_cart set quantity = #{quantity}, price = #{price}, update_time = cast(#{updateTime} as date) where user_id = #{userId} and product_id = #{productId}")
    void updateCart(@Param("userId")long userId, @Param("productId")long productId, @Param("quantity")int quantity,
                    @Param("price") double price, @Param("updateTime") Date updateTime);

    @Delete("delete from shopping_cart where user_id = #{userId} and product_id = #{productId}")
    void deleteByUserIdAndProductId(@Param("userId")long userId, @Param("productId")long productId);

    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(@Param("userId")long userId);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> selectByUserId(@Param("userId") long userId);

}
