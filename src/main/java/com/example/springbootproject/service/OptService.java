package com.example.springbootproject.service;

import com.example.springbootproject.repository.impl.OtpRecoveryRepository;
import com.example.springbootproject.security.otp.OtpAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OptService {

    @Autowired
    public OtpAuthenticator otpAuthenticator;

    @Autowired
    public OtpRecoveryRepository otpRecoveryRepository;

    public String getOptQrCode(String username, String secretKey){
        return otpAuthenticator.getQrCode(username, secretKey);
    }

    public List<String> getRecoveryCodesForUser(int userId){
        return otpRecoveryRepository.findRecoveryCodesById(userId);
    }

    public boolean verifyCode(String secretKey, String code){

        // one more step is to check if the provided code is normal or recovery code
        // TBD

        return otpAuthenticator.verifyTotpCode(secretKey, code);
    }
}
