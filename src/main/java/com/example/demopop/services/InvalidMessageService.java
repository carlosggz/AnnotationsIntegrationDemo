package com.example.demopop.services;

import com.example.demopop.config.ConstantsUtils;
import com.example.demopop.models.MailInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InvalidMessageService {

    @ServiceActivator(inputChannel = ConstantsUtils.INVALID_CHANNEL)
    public void process(MailInfoDto dto) {
        log.info("Processing invalid message: {}", dto);
    }
}
