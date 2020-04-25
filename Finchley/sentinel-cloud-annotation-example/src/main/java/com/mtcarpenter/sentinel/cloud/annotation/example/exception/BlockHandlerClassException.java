package com.mtcarpenter.sentinel.cloud.annotation.example.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Slf4j
public class BlockHandlerClassException {
    /**
     * 降级和限流
     * @param productId
     * @param key
     * @param ex
     * @return
     */
    public static String block(String productId,  String key, BlockException ex){
        log.warn("productId={}：热点数据被限流或者降级了 err={}",productId,ex.getMessage());
        return productId+"：热点数据被限流或者降级了";
    }
}
