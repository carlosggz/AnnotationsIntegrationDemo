package com.example.demopop.components;

import com.example.demopop.config.ConstantsUtils;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import javax.mail.internet.MimeMessage;

@MessagingGateway
public interface InboundMailGateway {

    @Gateway(requestChannel = ConstantsUtils.INBOUND_MAIL_CHANNEL)
    void send(MimeMessage mimeMessage);
}
