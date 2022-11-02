package com.example.springbootproject.config;


import com.jcraft.jsch.ChannelSftp;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.io.InputStream;

@Configuration
@IntegrationComponentScan
@EnableIntegration
public class SftpConfig {

    @Value("${app.sftp.host}")
    private String sftpHostName;

    @Value("${app.sftp.port}")
    private int sftpPort;

//    @Value("${app.sftp.privateKey}")
//    private String sftpPrivateKeyIdentity;

    @Value("${app.sftp.privateKey}")
    private Resource sftpPrivateKeyResource;

    @Value("${app.sftp.privateKeyPassphrase}")
    private String sftpPrivateKeyPassword;

//    @Value("${app.sftp.knownHostsFile}")
//    private String sftpKnownHostsFile;

    @Value("${app.sftp.knownHostsFile}")
    private Resource sftpKnownHostsResource;

    @Value("${app.sftp.user}")
    private String sftpUserName;

    @Value("${app.sftp.destinationDir}")
    private String sftpDestinationDir;


    @Bean
    public SessionFactory<ChannelSftp.LsEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(sftpHostName);
        factory.setPort(sftpPort);
        factory.setPrivateKey(sftpPrivateKeyResource);
        factory.setPrivateKeyPassphrase(sftpPrivateKeyPassword);
        factory.setUser(sftpUserName);
        factory.setAllowUnknownKeys(true);
        factory.setKnownHostsResource(sftpKnownHostsResource);

        CachingSessionFactory<ChannelSftp.LsEntry> cachingSessionFactory = new CachingSessionFactory(factory);
        cachingSessionFactory.setPoolSize(10);
        cachingSessionFactory.setSessionWaitTimeout(1000L); // 1 second
        cachingSessionFactory.setTestSession(true);

        return cachingSessionFactory;
    }

    @Bean
    @ServiceActivator(inputChannel = "toSftpChannel")
    public MessageHandler sftpMessageHandler() {
        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
        handler.setRemoteDirectoryExpression(new LiteralExpression(sftpDestinationDir));
        handler.setFileNameGenerator(message -> {

            if (message.getPayload() instanceof File) {
                return ((File) message.getPayload()).getName();
            } else if (message.getPayload() instanceof InputStream) {
                String fileName = message.getHeaders().get("FileName", String.class);
                if (Strings.isEmpty(fileName)) {
                    throw new IllegalArgumentException("File Name should be defined.");
                }
                return fileName;
            } else {
                throw new IllegalArgumentException("File (or InputStream) expected as payload.");
            }

        });

        return handler;
    }

}
