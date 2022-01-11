package com.example.demopop.config.queue;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "app.queue")
public class QueueSettings {
    private String exchange;
    private String routingKey;
}
