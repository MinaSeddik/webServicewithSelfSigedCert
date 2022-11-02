package com.example.springbootproject.sftp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class SftpClient {

    @Autowired
    private SFtpOutboundGateway sFtpOutboundGateway;

    public void putFile(File file) {

        sFtpOutboundGateway.uploadFile(file);

    }


}
