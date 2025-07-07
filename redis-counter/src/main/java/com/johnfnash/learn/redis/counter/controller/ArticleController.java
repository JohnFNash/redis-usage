package com.johnfnash.learn.redis.counter.controller;

import com.johnfnash.learn.redis.counter.service.ArticleCollectionService;
import com.johnfnash.learn.redis.counter.service.ArticleReadService;
import com.johnfnash.learn.redis.counter.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleReadService articleReadService;
    @Autowired
    private ArticleCollectionService articleCollectionService;

    @GetMapping("/add")
    public String add(@RequestParam(value = "title") String title, @RequestParam(value = "content") String content,
                      @RequestParam(value = "userId") Long userId) {
        articleService.add(title, content, userId);
        return "success";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam(value = "articleId")Long articleId, @RequestParam(value = "userId")Long userId) {
        articleService.delete(articleId, userId);
        return "success";
    }

    @GetMapping("/read")
    public String read(@RequestParam(value = "articleId")Long articleId, @RequestParam(value = "userId")Long userId) {
        articleReadService.add(userId, articleId);
        return "success";
    }

    @GetMapping("/collect")
    public String articleCollect(@RequestParam(value = "articleId")Long articleId, @RequestParam(value = "userId")Long userId) {
        articleCollectionService.add(articleId, userId);
        return "success";
    }

    @GetMapping("/unCollect")
    public String articleUnCollect(@RequestParam(value = "articleId")Long articleId, @RequestParam(value = "userId")Long userId) {
        articleCollectionService.delete(articleId, userId);
        return "success";
    }

}
