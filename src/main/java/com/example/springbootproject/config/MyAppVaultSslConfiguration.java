package com.example.springbootproject.config;

import org.springframework.core.io.FileSystemResource;
import org.springframework.vault.support.SslConfiguration;

import java.io.File;

public class MyAppVaultSslConfiguration {

    public static SslConfiguration create() {
        return SslConfiguration.forTrustStore(
                new FileSystemResource(new File("parent_directory", "keystore.jks")),
                "changeit");
    }

}
