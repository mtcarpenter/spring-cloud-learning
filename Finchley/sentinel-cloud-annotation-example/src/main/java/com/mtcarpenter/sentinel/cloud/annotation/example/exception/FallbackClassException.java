package com.mtcarpenter.sentinel.cloud.annotation.example.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Slf4j
public class FallbackClassException {

    /**
     * 限流降级 异常
     * @return
     */
    public static String fallback(String productId,  String key,Throwable ex){
        log.warn("热点数据被限流或者降级了",ex);
        return productId+"：热点数据被限流或者降级了";
    }
}
