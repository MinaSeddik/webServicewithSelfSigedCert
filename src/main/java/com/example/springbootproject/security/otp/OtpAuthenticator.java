package com.example.springbootproject.security.otp;

import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Component
@Slf4j
public class OtpAuthenticator {

    @Value("${app.name}")
    private String keyIssuer;

    public String generateSecretKey() {

        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String secret = secretGenerator.generate();
        return secret;
    }

    public boolean verifyTotpCode(String secretKey, String code) {

        TimeProvider timeProvider = new SystemTimeProvider();
//        TimeProvider timeProvider = new NtpTimeProvider("pool.ntp.org", 5000);


        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        // secret = the shared secret for the user
        // code = the code submitted by the user
        boolean successful = verifier.isValidCode(secretKey, code);

        log.info("secretKey: {}, code: {}, Verified: [{}]", secretKey, code, successful);

        return successful;
    }

    public List<String> generateRecoveryCodes() {

        // Generate 16 random recovery codes
        RecoveryCodeGenerator recoveryCodes = new RecoveryCodeGenerator();
        String[] codes = recoveryCodes.generateCodes(16);

        return List.of(codes);
    }


    public String getQrCode(String username, String secretKey) {

        QrData data = new QrData.Builder()
//                .label("example@example.com")
                .label(username)
                .secret(secretKey)
                .issuer(keyIssuer)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData;
        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            throw new RuntimeException(e);
        }

        String mimeType = generator.getImageMimeType();

        String dataUri = getDataUriForImage(imageData, mimeType);
        // dataUri = data:image/png;base64,iVBORw0KGgoAAAANSU...

        log.info("QR URI: {}", dataUri);
        return dataUri;

//        try {
//            String qrURI = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", keyIssuer, username, secretKey, keyIssuer);
//
//            String encodedQrCode = QR_GOOGLE_API_PREFIX + URLEncoder.encode(qrURI, "UTF-8");
//            log.info("QR URI: {}", encodedQrCode);
//
//            return encodedQrCode;
//        } catch (UnsupportedEncodingException ex) {
//            throw new IllegalStateException(ex);
//        }
    }


}
