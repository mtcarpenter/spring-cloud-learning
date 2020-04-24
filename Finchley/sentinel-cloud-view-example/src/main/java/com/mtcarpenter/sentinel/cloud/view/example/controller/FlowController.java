package com.mtcarpenter.sentinel.cloud.view.example.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.mtcarpenter.sentinel.cloud.view.example.service.FlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 山间木匠
 *
 */
@RestController
@RequestMapping("/flow")
public class FlowController {

    @Autowired
    private FlowService flowService;

    @GetMapping("/test")
    public String test(){
        return "mtcarpenter:test";
    }

    @RequestMapping("/test-a")
    public String testA(){
        return "mtcarpenter:test-a";
    }

    @GetMapping("/test-b")
    public String testB(){
        this.flowService.common();
        return "mtcarpenter:test-b";
    }

    @GetMapping("/test-c")
    public String testC(){
        this.flowService.common();
        return "mtcarpenter:test-c";
    }

}