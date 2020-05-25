package com.mtcarpenter.rabbitmq.example.direct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Component
@Slf4j
public class DirectReceiver {

    /**
     * 简单直连消息接收
     * @param message
     */
    @RabbitListener(queuesToDeclare=@Queue("direct.queue"))
    @RabbitHandler
    public void message(Message message) {
        log.info("message result = {}",message);
    }


}