package com.example.demopop.components;

import com.example.demopop.models.MailInfoDto;
import com.example.demopop.persistence.MailDocument;
import com.example.demopop.persistence.MailDocumentsRepository;
import com.example.demopop.services.LoggingService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingDtoInterceptorTest {

    @Mock
    MailDocumentsRepository documentsRepository;

    @InjectMocks
    LoggingService loggingService;

    @Captor
    ArgumentCaptor<MailDocument> documentCaptor;

    @Test
    void callPresendCallsRepository() {
        //given
        val givenDto = MailInfoDto.builder()
                .id("123")
                .subject("subject")
                .body("body")
                .fromAddress("email@example.com")
                .build();

        //when
        loggingService.logDto(givenDto);

        //then
        verify(documentsRepository).save(documentCaptor.capture());
        val doc = documentCaptor.getValue();
        assertEquals(givenDto.getId(), doc.getId());
        assertEquals(givenDto.getSubject(), doc.getSubject());
        assertEquals(givenDto.getBody(), doc.getBody());
        assertEquals(givenDto.getFromAddress(), doc.getFromAddress());
        assertNotNull(doc.getCreationDate());
    }

}