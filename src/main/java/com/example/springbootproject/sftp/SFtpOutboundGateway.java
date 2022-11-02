package com.example.springbootproject.sftp;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import java.io.File;

@MessagingGateway
public interface SFtpOutboundGateway {

    @Gateway(requestChannel = "toSftpChannel")
    void uploadFile(File file);

}