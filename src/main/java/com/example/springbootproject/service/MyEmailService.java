package com.example.springbootproject.service;


import lombok.extern.slf4j.Slf4j;
import net.markenwerk.utils.mail.smime.SmimeKey;
import net.markenwerk.utils.mail.smime.SmimeKeyStore;
import net.markenwerk.utils.mail.smime.SmimeUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

@Component
@Slf4j
public class MyEmailService implements InitializingBean {

    private final static String SIGNING_PRIVATE_KEY_ALIAS = "alias";

    @Value("${spring.mail.properties.mail.from}")
    String from;

    @Value("${app.privatekey.keystore.passwd}")
    String keyStorePassword;

    @Value("${app.privatekey.passwd}")
    String privateKeyPassword;

    @Value("classpath:smime.p12")
    private Resource pkcs12StoreResource;

    @Value("classpath:cert.pem")
    private Resource digitalCertificateResource;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void afterPropertiesSet() {
        log.debug("Adding Security provider *BouncyCastleProvider*");
        Security.addProvider(new BouncyCastleProvider());
    }

    public void sendSimpleTextMessage(String to, String subject, String text) {

        log.info("Sending email To: {}", to);
        log.debug("Sending email From: {}", from);
        log.debug("Sending email Subject: {}", subject);
        log.debug("Sending email Message: {}", text);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            // message.setCc(cc);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (Exception e) {
            log.error("{}: Can't send email, {}", e.getClass(), e);
        }

    }

    public void sendSimpleHtmlMessage(String to, String subject, String htmlMsg) {

        log.info("Sending html email To: {}", to);
        log.debug("Sending html email From: {}", from);
        log.debug("Sending html email Subject: {}", subject);
        log.debug("Sending html email Message: {}", htmlMsg);

        try {

            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // mimeMessage.setContent(htmlMsg, "text/html"); /** Use this or below line **/
            helper.setText(htmlMsg, true); // Use this or above line.

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            emailSender.send(mimeMessage);


        } catch (MessagingException e) {
            log.error("{}: Can't send email, {}", e.getClass(), e);
        }


    }

    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {

        log.info("Sending email To: {}", to);
        log.debug("Sending email From: {}", from);
        log.debug("Sending email Subject: {}", subject);
        log.debug("Sending email Message: {}", text);
        log.debug("Sending email with attachment file: {}", pathToAttachment);

        try {
            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);  // true for html message content-type


            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("WhateverFileName.pdf", file);

            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("{}: Can't send email, {}", e.getClass(), e);
        }

    }

    public void sendSignedAndEncryptedMessage(String to, String subject, String text, String pathToAttachment) {

        log.info("Sending email To: {}", to);
        log.debug("Sending email From: {}", from);
        log.debug("Sending email Subject: {}", subject);
        log.debug("Sending email Message: {}", text);
        log.debug("Sending email with attachment file: {}", pathToAttachment);

        try {

            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setSentDate(new Date());
            message.setHeader("Content-Transfer-Encoding", "base64");

            MimeBodyPart mimeEftsAttachBodyPart = new MimeBodyPart();
            File eftsFile = new File(pathToAttachment);
            mimeEftsAttachBodyPart.attachFile(eftsFile);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeEftsAttachBodyPart);
            message.setContent(multipart);

            Session session = ((JavaMailSenderImpl) emailSender).getSession();
            MimeMessage signedMessage = signMessage(session, message);
            MimeMessage encryptedSignedMessage = encryptMessage(session, signedMessage);

            emailSender.send(encryptedSignedMessage);

        } catch (MessagingException | IOException | CertificateException | NoSuchProviderException e) {
            log.error("{}: Can't send email, {}", e.getClass(), e);
        }

    }

    private MimeMessage signMessage(Session session, MimeMessage message) throws IOException, MessagingException {

        log.debug("Signing message: {}", message.getMessageID());
        SmimeKey smimeKey = getSmimeKeyForSender();

        log.debug("Signing message {} with private key: {}", message.getMessageID(), "<OBSCURED>");
        return SmimeUtil.sign(session, message, smimeKey);
    }

    private SmimeKey getSmimeKeyForSender() throws IOException {

        log.debug("Load PKCS12 store: {}, storepasswd: {}", pkcs12StoreResource.getFilename(), "<OBSCURED>");
        SmimeKeyStore smimeKeyStore = new SmimeKeyStore(pkcs12StoreResource.getInputStream(), keyStorePassword.toCharArray());

        log.debug("Get private key [alias='{}'] from PKCS12 store {}, private key password: {}", SIGNING_PRIVATE_KEY_ALIAS, pkcs12StoreResource.getFilename(), "<OBSCURED>");
        SmimeKey smimeKey = smimeKeyStore.getPrivateKey(SIGNING_PRIVATE_KEY_ALIAS, privateKeyPassword.toCharArray());

        return smimeKey;
    }

    private MimeMessage encryptMessage(Session session, MimeMessage message) throws CertificateException, NoSuchProviderException, MessagingException, IOException {

        log.debug("Encrypt message: {}", message.getMessageID());
        X509Certificate certificate = getCertificateForRecipient();

        log.debug("Signing message: {} with public key: {}", message.getMessageID(), "<OBSCURED>");
        return SmimeUtil.encrypt(session, message, certificate);
    }

    private X509Certificate getCertificateForRecipient() throws CertificateException, NoSuchProviderException, IOException {

        log.debug("Create CertificateFactory with BC provider");
        CertificateFactory factory = CertificateFactory.getInstance("X.509", "BC");

        log.debug("Load public key from digital certificate file: {}", digitalCertificateResource.getFilename());
        X509Certificate certificate = (X509Certificate) factory.generateCertificate(digitalCertificateResource.getInputStream());

        return certificate;
    }
}