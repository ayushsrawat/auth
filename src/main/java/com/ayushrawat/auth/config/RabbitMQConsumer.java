package com.ayushrawat.auth.config;

import com.ayushrawat.auth.payload.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

  private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);

  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(UserRegisteredEvent event) {
        logger.warn("User registered event received for user: {}", event.toString());
    }


}
