package com.johnfnash.learn.redis.shoppingcart.controller;

import com.johnfnash.learn.redis.shoppingcart.service.ShoppingCartService;
import com.johnfnash.learn.redis.shoppingcart.vo.ShoppingCartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("userId")long userId, @RequestParam("productId")long productId,
                            @RequestParam("quantity") int quantity, @RequestParam("price") double price) {
        shoppingCartService.addToCart(userId, productId, quantity, price);
        return "success";
    }

    @GetMapping("/deleteFromCart")
    public String deleteFromCart(@RequestParam("userId")long userId, @RequestParam("productId")long productId) {
        shoppingCartService.deleteFromCart(userId, productId);
        return "success";
    }

    @GetMapping("/updateCart")
    public String updateCart(@RequestParam("userId")long userId, @RequestParam("productId")long productId,
                             @RequestParam("quantity") int quantity, @RequestParam("price") double price) {
        shoppingCartService.updateCart(userId, productId, quantity, price);
        return "success";
    }

    @GetMapping("/getCart")
    public List<ShoppingCartVo> getCart(@RequestParam("userId")long userId) {
        return shoppingCartService.getCart(userId);
    }

    @GetMapping("/deleteCart")
    public String deleteCart(@RequestParam("userId")long userId) {
        shoppingCartService.clearCart(userId);
        return "success";
    }

}
