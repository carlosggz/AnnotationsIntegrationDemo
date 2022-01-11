package com.example.demopop.config.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QueueConfiguration {
    private final QueueSettings queueSettings;

    @Bean
    Queue demoQueue() {
        //queue to read the exchange and see the messages
        return new Queue("queue." + queueSettings.getExchange(), true);
    }

    @Bean
    Exchange demoExchange() {
        return new TopicExchange(queueSettings.getExchange(), true, false);
    }

    @Bean
    Binding queueBinding(Queue demoQueue, Exchange demoExchange) {
        return BindingBuilder
                .bind(demoQueue)
                .to(demoExchange)
                .with(queueSettings.getRoutingKey())
                .noargs();
    }
}
