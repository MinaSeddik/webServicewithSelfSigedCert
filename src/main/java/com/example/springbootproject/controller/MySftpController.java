package com.example.springbootproject.controller;

import com.example.springbootproject.sftp.SftpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.util.Map;

@RestController
@Slf4j
public class MySftpController {

    @Value("${app.sftp.sampleFile}")
    private Resource sampleFile;

    @Autowired
    public SftpClient sftpClient;

    @Autowired
    MessageHandler sftpMessageHandler;

    @RequestMapping(value = "/sftp")
    public void sftp() {

        File file = null;
        try {
            log.info("Get the sample file to upload to the SFTP server");
            file = sampleFile.getFile();
        } catch (IOException e) {
            log.error("Can't get the sample file to upload to the SFTp server: {}", e);
        }

        sftpClient.putFile(file);
    }


    @RequestMapping(value = "/sftp2")
    public void sftp2() {

        InputStream inputStream = null;
        try {
            log.info("Get the sample file to upload to the SFTP server As InputStream");
            inputStream = sampleFile.getInputStream();
        } catch (IOException e) {
            log.error("Can't get the sample file to upload to the SFTp server: {}", e);
        }

        MailMessage mailMessage = new SimpleMailMessage();
        new GenericMessage<>(mailMessage);


        Message<InputStream> message = new GenericMessage<>(inputStream, Map.of("FileName", sampleFile.getFilename()));


        sftpMessageHandler.handleMessage(message);
    }


}
