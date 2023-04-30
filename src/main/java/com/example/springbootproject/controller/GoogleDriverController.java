package com.example.springbootproject.controller;

import com.example.springbootproject.domain.DriveFiles;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;

@RestController
@Slf4j
public class GoogleDriverController {

    /*
    @Autowired
    private OAuth2RestTemplate restTemplate;

    @RequestMapping("/google-driver")
    public DriveFiles google_driver() {


        // list files in the driver
        String url = "https://www.googleapis.com/drive/v3/files";
        DriveFiles driveFiles = restTemplate.getForObject(url, DriveFiles.class);
//        String driveFiles = restTemplate.getForObject("https://www.googleapis.com/drive/v3/files", String.class);
        log.info("####################### DATA: {}", driveFiles);


        // upload file to the driver



        return driveFiles;
    }
*/

    @RequestMapping("/google-upload")
    private String uploadFileToDrive() throws IOException, GeneralSecurityException {

        String googleCredentialFileName = "client_secret_1052945576635-vv55b7jprva8ss2vs02lgdj0d4dh70u6.apps.googleusercontent.com.json";

        log.info("load JSON Config File: {}", googleCredentialFileName);
        InputStream resourceAsStream = GoogleDriverController.class.
                getClassLoader()
                .getResourceAsStream(googleCredentialFileName);

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(resourceAsStream);

        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(googleCredentials);

        // Build a new authorized API client service.
        log.info("Build Drive Service .....");
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();


        log.info("uploading file " + "/home/mina/Desktop/bear.jpg");

        // Upload file photo.jpg on drive.
        File fileMetadata = new File();
        fileMetadata.setName("photo.jpg");
        // File's content.
        java.io.File filePath = new java.io.File("/home/mina/Desktop/bear.jpg");
        // Specify media type and file-path for file.
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        try {
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            log.error("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }

    /*
    private static Credential getCredentials(final NetHttpTransport transport) throws IOException {
        AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(transport,
                GsonFactory.getDefaultInstance(),
                "1004637461184-eq1nlfkc5n2tk85ha6b5v54jl2aqdgmm.apps.googleusercontent.com",    // Client ID
                "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",                                          // Client Secret
                Arrays.asList(GmailScopes.GMAIL_SEND))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
*/
/*
    public DriveFiles getDriveFiles(String accessToken) {
        String requestUri = "https://www.googleapis.com/drive/v3/files";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity request = new HttpEntity(headers);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
                new MappingJackson2HttpMessageConverter();
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
        ResponseEntity<String> response = restTemplate.exchange(requestUri, HttpMethod.GET, request, String.class);

        Gson gson = new Gson();
        DriveFiles driveFiles = gson.fromJson(response.getBody(), DriveFiles.class);

        return driveFiles;
    }
*/

}
