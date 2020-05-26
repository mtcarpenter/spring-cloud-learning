package com.mtcarpenter.rabbitmq.example.topic;

import com.mtcarpenter.rabbitmq.example.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Component
@Slf4j
public class TopicSender implements RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 发送消息方法调用: 构建Message消息
     * @param book
     * @throws Exception
     */
    public void send(Book book) throws Exception {
        //设置消息退回后的回调处理机制
        this.rabbitTemplate.setReturnCallback(this);
        // CorrelationData是一个当发送原始消息时，由客户机提供的对象。
        // ack是一个boolean值，当ack（确认）的时候，值为true；当nack（不确认）的时候，值为false
        // 经测试发现，只要 rabbitTemplate.convertAndSend() 能正确找到exchange，无论是否能将消息路由到正确的queue,ack值都为true
        // 只有当rabbitTemplate.convertAndSend()无法找到exchange时，ack 值才为false
        // cause是附加原因，比如说当nack的时候附加的原因
        this.rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送失败 cause = {} ,correlationData={} ", cause, correlationData.toString());
            } else {
                log.info("消息发送成功 ");
            }
        });
        this.rabbitTemplate.convertAndSend("order", "encyclopedia", book);
    }


    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("return message={} , exchange = {} , routingKey = {},replyCode={}, replyText{}",
                message, exchange, routingKey, replyCode, replyText);

    }
}