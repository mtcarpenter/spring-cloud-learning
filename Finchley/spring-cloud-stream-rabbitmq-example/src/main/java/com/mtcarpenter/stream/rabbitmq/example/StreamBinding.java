package com.mtcarpenter.stream.rabbitmq.example;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author mtcarpenter
 * @github https://github.com/mtcarpenter/spring-cloud-learning
 * @desc 微信公众号：山间木匠
 */
public interface StreamBinding {

    String INPUT = "input";

    String OUTPUT = "output";


    @Input(StreamBinding.INPUT)
    SubscribableChannel input();

    @Output(StreamBinding.OUTPUT)
    MessageChannel output();
}