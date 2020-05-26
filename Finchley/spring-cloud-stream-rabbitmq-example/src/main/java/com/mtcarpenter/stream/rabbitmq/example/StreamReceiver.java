package com.mtcarpenter.stream.rabbitmq.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
@Component
@EnableBinding(StreamBinding.class)
@Slf4j
public class StreamReceiver {
    @StreamListener(StreamBinding.INPUT)
    public int inputMessage(Message message) {
        log.info("StreamReceiver:{}", message);
        return 0;
    }


}