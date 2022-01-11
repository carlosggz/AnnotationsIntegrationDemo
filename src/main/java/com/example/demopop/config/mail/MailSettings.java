package com.example.demopop.config.mail;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "app.pop3")
public class MailSettings {
    private String server;
    private int port;
    private boolean ssl;
    private String user;
    private String pwd;
    private String email;
    private boolean debug;
}
