package com.example.demopop.components;

import com.example.demopop.config.ConstantsUtils;
import com.example.demopop.models.MailInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class DtoTransformerComponent {
    private final ObjectMapper objectMapper;

    @Transformer(inputChannel = ConstantsUtils.CANCELLATION_CHANNEL, outputChannel = ConstantsUtils.OUTBOUND_FTP_CHANNEL)
    public File dtoToFile(@NonNull MailInfoDto mailInfoDto) {
        try {
            log.info("Transforming dto to file...");
            val file = Path.of(System.getProperty("java.io.tmpdir"), mailInfoDto.getId()  + ".json").toFile();
            objectMapper.writeValue(file, mailInfoDto);
            log.info("Sending to ftp channel...");
            return file;
        }
        catch (Exception ex) {
            log.error("Error converting dto to file: {}", ex.getMessage());
            return null;
        }
    }

    @Transformer(inputChannel = ConstantsUtils.CONFIRMATION_CHANNEL, outputChannel = ConstantsUtils.OUTBOUND_QUEUE_CHANNEL)
    public String dtoToJson(@NonNull MailInfoDto mailInfoDto) {
        try {
            log.info("Transforming dto to string...");
            val json = objectMapper.writeValueAsString(mailInfoDto);
            log.info("Sending to queue channel...");
            return json;
        }
        catch (Exception ex) {
            log.error("Error converting dto to string: {}", ex.getMessage());
            return null;
        }
    }
}
