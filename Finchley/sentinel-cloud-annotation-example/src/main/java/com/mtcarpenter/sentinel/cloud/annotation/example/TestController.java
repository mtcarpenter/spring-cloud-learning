package com.mtcarpenter.sentinel.cloud.annotation.example;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.mtcarpenter.sentinel.cloud.annotation.example.exception.BlockHandlerClassException;
import com.mtcarpenter.sentinel.cloud.annotation.example.exception.FallbackClassException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping(value = "/hello")
    @SentinelResource(value = "hello", blockHandler = "block")
    public String hello() {
        return "mtcarpenter:hello";
    }

    /**
     * 限流降级
     *
     * @param ex
     * @return
     */
    public String block(BlockException ex) {
        log.warn("服务被限流或者降级了 block", ex.getMessage());
        return "服务被限流或者降级了 block";
    }


    @GetMapping(value = "/degrade")
    @SentinelResource(value = "degrade", blockHandler = "degradeBlock", fallback = "degradeFallback")
    public String apiHello(@RequestParam(required = false) String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("参数为空");
        }
        return "mtcarpenter:" + name;
    }

    public String degradeBlock(String name, BlockException ex) {
        log.warn("服务被限流或者降级了 block", name, ex.getMessage());
        return "服务被限流或者降级了 block";
    }

    /**
     * 限流降级 异常
     *
     * @return
     */
    public String degradeFallback(String name) {
        log.warn("服务被限流或者降级了 异常 fallback");
        return "服务被限流或者降级了 异常 fallback";
    }


    @GetMapping(value = "/hot")
    @SentinelResource(
            value = "hot",
            blockHandler = "block",
            blockHandlerClass = BlockHandlerClassException.class,
            fallback = "fallback",
            fallbackClass = FallbackClassException.class)
    public String hot(@RequestParam("productId") String productId, @RequestParam("key") String key) {
        return productId + "---" + key;
    }


}
