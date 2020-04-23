package com.mtcarpenter.sentinel.cloud.view.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 山间木匠
 *
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/echo")
    public String echo(){
        return "mtcarpenter";
    }
}
