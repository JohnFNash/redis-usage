package com.johnfnash.learn.redis.set.controller;

import com.johnfnash.learn.redis.set.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping("/init")
    public String init() {
        candidateService.init(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        return "success";
    }

    @GetMapping("/start")
    public List<String> candidate() {
        return candidateService.candidate(3);
    }

}
