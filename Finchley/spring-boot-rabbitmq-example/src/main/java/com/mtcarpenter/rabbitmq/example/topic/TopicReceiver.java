package com.mtcarpenter.rabbitmq.example.topic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 *
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Component
@Slf4j
public class TopicReceiver {


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "magazineOrder",durable = "true"),
            exchange = @Exchange(value = "order",type = "topic"),
            key = "magazine"
    ))
    @RabbitHandler
    public void magazineMessage(Message message) {
        log.info("magazine result = {}",message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "historyOrder",durable = "true"),
            exchange = @Exchange(value = "order",type = "topic"),
            key = "history"
    ))
    @RabbitHandler
    public void historyMessage(Message message) {
        log.info("history result = {}",message);
    }
}

