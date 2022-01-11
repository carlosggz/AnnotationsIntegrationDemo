package com.example.demopop.components;

import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class MailTransformerComponentTest {

    @Test
    @SneakyThrows
    void givenAMimeMessageItReturnsADto() {
        //given
        val givenSubject = "subject";
        val givenAddress = "email@example.com";
        val givenContent = "body";

        val mimeMessage = new MimeMessage((Session)null);
        mimeMessage.setSubject(givenSubject);
        mimeMessage.setFrom(givenAddress);
        mimeMessage.setContent(givenContent, "Text/Plain");

        val mailTransformerComponent = new MailTransformerComponent();

        //when
        val actualResult = mailTransformerComponent.mailToDto(mimeMessage);

        //then
        assertNotNull(actualResult);
        assertEquals(givenSubject, actualResult.getSubject());
        assertEquals(givenAddress, actualResult.getFromAddress());
        assertNotNull(UUID.fromString(actualResult.getId()));
        assertEquals(givenContent, actualResult.getBody());
    }

    @Test
//    @SneakyThrows
    void givenAMimeMessageWithoutDataItReturnsAnEmptyDto() {
        //given
        val expectedContent = "n/a";
        val mimeMessage = new MimeMessage((Session)null);
        val mailTransformerComponent = new MailTransformerComponent();

        //when
        val actualResult = mailTransformerComponent.mailToDto(mimeMessage);

        //then
        assertNotNull(actualResult);
        assertEquals(expectedContent, actualResult.getSubject());
        assertEquals(expectedContent, actualResult.getFromAddress());
        assertDoesNotThrow(() -> UUID.fromString(actualResult.getId()));
        assertEquals(expectedContent, actualResult.getBody());
    }

}