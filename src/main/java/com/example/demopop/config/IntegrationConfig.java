package com.example.demopop.config;

import com.example.demopop.components.RemoveFileInterceptorComponent;
import com.example.demopop.config.queue.QueueSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.http.HttpMethod;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.outbound.FtpMessageHandler;
import org.springframework.integration.handler.MessageHandlerChain;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class IntegrationConfig {

    private final MailReceiver mailReceiver;
    private final QueueSettings queueSettings;

    @Bean(name = ConstantsUtils.INBOUND_MAIL_CHANNEL)
    MessageChannel mailChannel() { return new DirectChannel(); }

    @Bean(name = ConstantsUtils.ROUTING_CHANNEL)
    MessageChannel routingChannel() { return new PublishSubscribeChannel(); }

    @Bean(name = ConstantsUtils.OUTBOUND_FTP_CHANNEL)
    MessageChannel ftpChannel() {
        val channel = new DirectChannel();
        channel.addInterceptor(new RemoveFileInterceptorComponent());
        return channel;
    }

    @Bean(name = ConstantsUtils.INVALID_CHANNEL)
    MessageChannel invalidChannel() { return new DirectChannel(); }

    @Bean
    @InboundChannelAdapter(value = ConstantsUtils.INBOUND_MAIL_CHANNEL, poller = @Poller(fixedDelay = "${app.pollerMs}"))
    @ConditionalOnProperty(value="app.faker.enable", havingValue = "false", matchIfMissing = true)
    public MessageSource<?> mailReceiver() {
        return new MailReceivingMessageSource(mailReceiver);
    }

    @Bean
    @ServiceActivator(inputChannel = ConstantsUtils.OUTBOUND_FTP_CHANNEL)
    public MessageHandler outboundFtpHandler(SessionFactory<FTPFile> sessionFactory) {
        val handler = new FtpMessageHandler(sessionFactory);
        handler.setFileNameGenerator(message -> ((File)message.getPayload()).getName());
        handler.setRemoteDirectoryExpression(new LiteralExpression(""));
        handler.setLoggingEnabled(false);
        handler.setUseTemporaryFileName(true);
        handler.setShouldTrack(true);
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel = ConstantsUtils.OUTBOUND_QUEUE_CHANNEL)
    public MessageHandler outboundAmqpHandler(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
        outbound.setExchangeName(queueSettings.getExchange());
        outbound.setRoutingKey(queueSettings.getRoutingKey());
        return outbound;
    }

    @Bean
    @ServiceActivator(inputChannel = ConstantsUtils.OUTBOUND_HTTP_CHANNEL)
    public MessageHandler outboundHttpChain(@Value("${app.http.url}") String url) {
        val chain = new MessageHandlerChain();
        chain.setHandlers(List.of(outboundHttpHandler(url), responseHttpHandler() ));
        return chain;
    }

    MessageHandler outboundHttpHandler(String url) {
        HttpRequestExecutingMessageHandler handler = new HttpRequestExecutingMessageHandler(url);
        handler.setHttpMethod(HttpMethod.POST);
        handler.setLoggingEnabled(false);
        handler.setExpectedResponseType(String.class);
        handler.setExpectReply(true);
        return handler;
    }

    MessageHandler responseHttpHandler() {
        return message -> log.info("Response from http: {}", message.getPayload());
    }
}
