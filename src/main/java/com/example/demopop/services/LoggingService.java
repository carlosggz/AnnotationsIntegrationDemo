package com.example.demopop.services;

import com.example.demopop.config.ConstantsUtils;
import com.example.demopop.models.MailInfoDto;
import com.example.demopop.persistence.MailDocument;
import com.example.demopop.persistence.MailDocumentsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingService {
    private final MailDocumentsRepository documentsRepository;

    @ServiceActivator(inputChannel = ConstantsUtils.ROUTING_CHANNEL)
    public void logDto(@NonNull MailInfoDto mailInfoDto) {
        log.info("Saving mail content: {}", mailInfoDto);
        documentsRepository.save(MailDocument.builder()
                .id(mailInfoDto.getId())
                .subject(mailInfoDto.getSubject())
                .body(mailInfoDto.getBody())
                .fromAddress(mailInfoDto.getFromAddress())
                .creationDate(LocalDateTime.now())
                .build());
        log.info("Mail saved");
    }
}
