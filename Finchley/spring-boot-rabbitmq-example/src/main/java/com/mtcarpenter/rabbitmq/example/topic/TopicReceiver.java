package com.mtcarpenter.rabbitmq.example.topic;

import com.mtcarpenter.rabbitmq.example.Book;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

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


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "encyclopediaOrder",durable = "true"),
            exchange = @Exchange(value = "order",type = "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "encyclopedia"
    ))
    @RabbitHandler
    public void onOrderMessage(@Payload Book book,
                               Channel channel,
                               @Headers Map<String, Object> headers) throws Exception {
       log.info("book id = {}" + book.getId());
        Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }
}

