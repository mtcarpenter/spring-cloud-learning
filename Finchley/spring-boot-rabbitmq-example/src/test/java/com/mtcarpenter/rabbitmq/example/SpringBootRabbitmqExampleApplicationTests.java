package com.mtcarpenter.rabbitmq.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRabbitmqExampleApplicationTests {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void directReceiver() {
        Book book = new Book(1,"spring cloud","mtcarpenter");
        amqpTemplate.convertAndSend("direct.queue",book);
    }

    @Test
    public void topicReceiver(){
        Book book1 = new Book(1,"spring cloud 1","mtcarpenter1");
        amqpTemplate.convertAndSend("topic.exchange","topic.queue",book1);
        Book book2 = new Book(2,"spring cloud 2","mtcarpenter2");
        amqpTemplate.convertAndSend("topic.exchange","topic.queue1",book2);
    }

}
