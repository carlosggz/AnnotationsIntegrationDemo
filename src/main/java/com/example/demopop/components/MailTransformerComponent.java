package com.example.demopop.components;

import com.example.demopop.config.ConstantsUtils;
import com.example.demopop.models.MailInfoDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailTransformerComponent {
    private static final String NOT_AVAILABLE = "n/a";

    @Transformer(inputChannel = ConstantsUtils.INBOUND_MAIL_CHANNEL, outputChannel = ConstantsUtils.ROUTING_CHANNEL)
    public MailInfoDto mailToDto(@NonNull MimeMessage mimeMessage) {
        log.info("Parsing mail...");
        val dto = MailInfoDto.builder()
                .id(UUID.randomUUID().toString())
                .fromAddress(getAddress(mimeMessage).orElse(NOT_AVAILABLE))
                .subject(getSubject(mimeMessage).orElse(NOT_AVAILABLE))
                .body(getBody(mimeMessage).orElse(NOT_AVAILABLE))
                .build();
        log.info("Parsed mail, sending to routing...");
        return dto;
    }

    private Optional<String> getBody(MimeMessage mimeMessage) {
        try {
            val parser = new MimeMessageParser(mimeMessage).parse();
            val body = parser.hasHtmlContent() ? parser.getHtmlContent() : parser.getPlainContent();
            return Optional.ofNullable(body);
        }
        catch (Exception ex) {
            log.error("Error parsing body", ex);
            return Optional.empty();
        }
    }

    private Optional<String> getSubject(MimeMessage mimeMessage) {
        try {
            return Optional.of(mimeMessage.getSubject());
        }
        catch (Exception ex) {
            log.error("Error parsing subject", ex);
            return Optional.empty();
        }
    }

    private Optional<String> getAddress(MimeMessage mimeMessage) {
        try {
            return Optional.ofNullable(mimeMessage.getFrom())
                    .flatMap(x -> Arrays.stream(x).findFirst())
                    .map(x -> (InternetAddress)x)
                    .map(InternetAddress::getAddress);
        }
        catch (Exception ex) {
            log.error("Error parsing subject", ex);
            return Optional.empty();
        }
    }
}
