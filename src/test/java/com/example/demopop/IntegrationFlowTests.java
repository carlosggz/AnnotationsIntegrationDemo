package com.example.demopop;

import com.example.demopop.components.InboundMailGateway;
import com.example.demopop.models.MailInfoDto;
import com.example.demopop.persistence.MailDocumentsRepository;
import com.example.demopop.services.InvalidMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.integration.test.mock.MockIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.annotation.DirtiesContext;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@SpringBootTest
@SpringIntegrationTest(noAutoStartup = {"inboundChannelAdapter"})
@DirtiesContext
public class IntegrationFlowTests {

    static final String GIVEN_ADDRESS = "test@example.com";
    static final String GIVEN_BODY = "body";

    @Autowired
    MailDocumentsRepository documentsRepository;

    @SpyBean
    InvalidMessageService invalidMessageService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    @Qualifier("outboundFtpHandler")
    MessageHandler outboundFtpHandler;

    @MockBean
    @Qualifier("outboundAmqpHandler")
    MessageHandler outboundAmqpHandler;

    @MockBean
    @Qualifier("outboundHttpChain")
    MessageHandler outboundHttpChain;

    @Autowired
    InboundMailGateway inboundMailGateway;

    @BeforeEach
    void setup() {
        documentsRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void whenAMessageArrivesToTheChannelItIsSaved() {
        //given
        val givenSubject = "subject";
        val mimeMessage = getMimeMessage(givenSubject);

        //when
        inboundMailGateway.send(mimeMessage);

        //then
        val allSaved = documentsRepository.findAll();
        assertEquals(1, allSaved.size());
        val saved = allSaved.get(0);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreationDate());
        assertEquals(givenSubject, saved.getSubject());
        assertEquals(GIVEN_ADDRESS, saved.getFromAddress());
        assertEquals(GIVEN_BODY, saved.getBody());
    }

    @Test
    @SneakyThrows
    void whenAnInvalidMessagesArrivesItGoesToTheInvalidChannel() {
        //given
        val givenSubject = "Unexpected subject";
        val mimeMessage = getMimeMessage(givenSubject);
        doNothing().when(invalidMessageService).process(any(MailInfoDto.class));

        //when
        inboundMailGateway.send(mimeMessage);

        //then
        val captor = ArgumentCaptor.forClass(MailInfoDto.class);
        verify(invalidMessageService).process(captor.capture());
        assertMessageMatches(givenSubject, captor.getValue());
    }

    @Test
    @SneakyThrows
    void whenACancellationMessagesArrivesItGoesToTheFtpChannel() {
        //given
        ArgumentCaptor<Message<?>> messageArgumentCaptor = MockIntegration.messageArgumentCaptor();
        val mimeMessage = getMimeMessage("Cancellation - [123]");

        //when
        inboundMailGateway.send(mimeMessage);

        //then
        verify(outboundFtpHandler).handleMessage(messageArgumentCaptor.capture());
        val ftpFile = (File) messageArgumentCaptor.getValue().getPayload();
        assertNotNull(ftpFile);
        assertFalse(ftpFile.exists()); //Already removed by next handler
    }

    @Test
    @SneakyThrows
    void whenAConfirmationMessagesArrivesItGoesToTheQueueChannel() {
        //given
        ArgumentCaptor<Message<?>> messageArgumentCaptor = MockIntegration.messageArgumentCaptor();
        val givenSubject = "Confirmation - [123]";
        val mimeMessage = getMimeMessage(givenSubject);

        //when
        inboundMailGateway.send(mimeMessage);

        //then
        verify(outboundAmqpHandler).handleMessage(messageArgumentCaptor.capture());
        val json = (String) messageArgumentCaptor.getValue().getPayload();
        assertNotNull(json);
        val dto = objectMapper.readValue(json, MailInfoDto.class);
        assertMessageMatches(givenSubject, dto);
    }

    @Test
    @SneakyThrows
    void whenAnInfoRequireMessagesArrivesItGoesToTheHttpChannel() {
        //given
        ArgumentCaptor<Message<?>> messageArgumentCaptor = MockIntegration.messageArgumentCaptor();
        val givenSubject = "Info Required - [123]";
        val mimeMessage = getMimeMessage(givenSubject);

        //when
        inboundMailGateway.send(mimeMessage);

        //then
        verify(outboundHttpChain).handleMessage(messageArgumentCaptor.capture());
        val dto = (MailInfoDto) messageArgumentCaptor.getValue().getPayload();
        assertMessageMatches(givenSubject, dto);
    }

    private void assertMessageMatches(String subject, MailInfoDto dto){
        assertNotNull(dto);
        assertEquals(subject, dto.getSubject());
        assertEquals(GIVEN_ADDRESS, dto.getFromAddress());
        assertEquals(GIVEN_BODY, dto.getBody());
        assertFalse(StringUtils.isBlank(dto.getId()));
    }

    private static MimeMessage getMimeMessage(String subject) throws MessagingException {
        val mimeMessage = new MimeMessage((Session) null);
        mimeMessage.setSubject(subject);
        mimeMessage.setFrom(GIVEN_ADDRESS);
        mimeMessage.setContent(GIVEN_BODY, "Text/Plain");
        return mimeMessage;
    }
}

