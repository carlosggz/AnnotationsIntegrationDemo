package com.example.demopop.components;

import com.example.demopop.config.mail.MailSettings;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Component
@Slf4j
@ConditionalOnProperty(value="app.faker.enable", havingValue = "true", matchIfMissing = false)
public class SendMailsComponent {
    final static List<String> SUBJECTS = List.of("Info Required", "Confirmation", "Cancellation", "Other");

    private final MailSettings mailSettings;
    private final InboundMailGateway inboundMailGateway;

    public SendMailsComponent(MailSettings mailSettings, InboundMailGateway inboundMailGateway) {
        this.mailSettings = mailSettings;
        this.inboundMailGateway = inboundMailGateway;
        log.info("Fake mailer started!!!");
    }

    @Scheduled(cron = "${app.faker.cron}")
    void sendMail() {
        try {
            val message = getMessage();
            log.info("Sending mail with subject: {}", message.getSubject());
            inboundMailGateway.send(message);
            log.info("Mail was successfully sent");
        } catch (Exception ex) {
            log.error("Error sending mail: {}", ex.getMessage());
        }
    }

    private MimeMessage getMessage() throws MessagingException {
        val mimeMessage = new MimeMessage((Session)null);
        mimeMessage.setSubject(getSubject());
        mimeMessage.setFrom(mailSettings.getEmail());
        mimeMessage.setContent("Content of the mail", "Text/Plain");
        return mimeMessage;
    }

    private String getSubject() {
        val action = SUBJECTS.get(RandomUtils.nextInt(0, SUBJECTS.size()));
        val reservationNumber = RandomUtils.nextInt(1, 100);
        return String.format("%s - [%s]", action, reservationNumber);
    }
}
