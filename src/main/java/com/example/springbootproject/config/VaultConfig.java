package com.example.springbootproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.support.SslConfiguration;

@Configuration
public class VaultConfig extends AbstractVaultConfiguration {

    @Override
    public VaultEndpoint vaultEndpoint() {
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setScheme("http");


//        VaultEndpoint vaultEndpoint = VaultEndpoint.create("localhost", 8200);
//        vaultEndpoint.setScheme("https");


        return endpoint;
    }

    @Override
    public ClientAuthentication clientAuthentication() {
//        return new TokenAuthentication("00000000-0000-0000-0000-000000000000");

//        vault server -dev -dev-root-token-id="root"
        return new TokenAuthentication("root");
    }

    /*
    @Override
    public SslConfiguration sslConfiguration() {
        return MyAppVaultSslConfiguration.create();
    }
    */

}