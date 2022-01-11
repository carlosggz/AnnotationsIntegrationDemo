package com.example.demopop.config.mail;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.Pop3MailReceiver;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfiguration {
    private final MailSettings mailSettings;

    @Bean
    public MailReceiver getPop3Receiver() {
        val receiver = new Pop3MailReceiver(mailSettings.getServer(), mailSettings.getPort(), mailSettings.getUser(), mailSettings.getPwd());
        receiver.setShouldDeleteMessages(true);
        receiver.setMaxFetchSize(1);
        receiver.setAutoCloseFolder(true);
        receiver.setSimpleContent(true);
        receiver.setJavaMailProperties(getProperties());
        return receiver;
    }

    private Properties getProperties() {
        val mailProperties = new Properties();
        mailProperties.setProperty("mail.pop3.ssl.enable", String.valueOf(mailSettings.isSsl()));
        mailProperties.setProperty("mail.debug", String.valueOf(mailSettings.isDebug()));
        mailProperties.setProperty("mail.pop3.auth", "true");
        mailProperties.setProperty("mail.pop3.ssl.trust", "*");
        return mailProperties;
    }
}
