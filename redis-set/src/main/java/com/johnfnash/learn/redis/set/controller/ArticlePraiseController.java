package com.johnfnash.learn.redis.set.controller;

import com.johnfnash.learn.redis.set.service.ArticlePraiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/praise")
public class ArticlePraiseController {

    @Autowired
    private ArticlePraiseService articlePraiseService;

    @GetMapping("/add")
    public String addPraise(String userId, String articleId) {
        articlePraiseService.addPraise(userId, articleId);
        return "success";
    }

    @GetMapping("/remove")
    public String removePraise(String userId, String articleId) {
        articlePraiseService.removePraise(userId, articleId);
        return "success";
    }

    @GetMapping("/count")
    public Long getPraiseCount(String articleId) {
        return articlePraiseService.getPraiseCount(articleId);
    }

    @GetMapping("/list")
    public Set<String> getPraiseUserList(String articleId) {
        return articlePraiseService.getPraiseUserList(articleId);
    }

}
