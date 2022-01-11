package com.example.demopop.components;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import java.io.File;

@Slf4j
public class RemoveFileInterceptorComponent implements ChannelInterceptor {

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        removeFile((File)message.getPayload());
        ChannelInterceptor.super.postSend(message, channel, sent);
    }

    private void removeFile(File file) {
        log.info("Removing temporary file {}...", file.getAbsolutePath());

        if (file.exists()) {
            val result = file.delete();
            log.info("Removed: {}", result);
        }
        else {
            log.info("File does not exists");
        }
    }
}
