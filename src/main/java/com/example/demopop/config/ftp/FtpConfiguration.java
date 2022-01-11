package com.example.demopop.config.ftp;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;

@Configuration
@RequiredArgsConstructor
public class FtpConfiguration {
    private final FtpSettings ftpSettings;

    @Bean
    SessionFactory<FTPFile> defaultFtpSessionFactory() {
        val defaultFtpSessionFactory = new DefaultFtpSessionFactory();
        defaultFtpSessionFactory.setHost(ftpSettings.getHost());
        defaultFtpSessionFactory.setPort(ftpSettings.getPort());
        defaultFtpSessionFactory.setUsername(ftpSettings.getUser());
        defaultFtpSessionFactory.setPassword(ftpSettings.getPwd());

        if (ftpSettings.isPassive()) {
            defaultFtpSessionFactory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
        }

        return new CachingSessionFactory<>(defaultFtpSessionFactory);
    }
}

