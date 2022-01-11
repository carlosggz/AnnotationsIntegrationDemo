package com.example.demopop.config.ftp;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "app.ftp")
public class FtpSettings {
    private String host;
    private int port;
    private String user;
    private String pwd;
    private boolean passive;
}
