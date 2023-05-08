package com.example.springbootproject.service;

import com.example.springbootproject.domain.Credential;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class VaultService {

    @Autowired
    private VaultTemplate vaultTemplate;

    public String tryVault() {

        log.info("-------------> Call tryVault ...   (1)");

        VaultKeyValueOperations vaultKeyValueOperations =
                vaultTemplate.opsForKeyValue("secret",  // the mount path
                        VaultKeyValueOperationsSupport.KeyValueBackend.versioned());
//        VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);


        log.info("-------------> Call tryVault ...   (2)");
//        vaultKeyValueOperations.put("secret/data/my-app", "Mina");
        Map<String, String> data = new HashMap<>();
        data.put("name", "Mina2");
        data.put("pwd", "OpenSemsem2");
        vaultKeyValueOperations.put("my-app", data);


        log.info("-------------> Call tryVault ...   (3)");
        VaultResponseSupport<Map> response = vaultKeyValueOperations.get("my-app", Map.class);


        log.info("-------------> Call tryVault ...   (4)");
        String username = (String) response.getData().get("name");
        String pwd = (String) response.getData().get("pwd");

        log.info("--------------------------------- " + VaultKeyValueOperationsSupport.KeyValueBackend.versioned());

        log.info("-------------> Call tryVault ...   (5)");
        Credential credential = new Credential();
        credential.setUsername("Cred-Name");
        credential.setPassword("Cred-PWD");
        vaultKeyValueOperations.put("my-app", credential);


        log.info("-------------> Call tryVault ...   (6)");
        VaultResponseSupport<Credential> response2 = vaultKeyValueOperations.get("my-app", Credential.class);


        log.info("-------------> Call tryVault ...   (7)");
        username = response2.getData().getUsername();
        pwd = (String) response2.getData().getPassword();


        return username + ":" + pwd;
    }

}
