package com.example.springbootproject.controller;

import com.example.springbootproject.dto.RestErrorResponse;
import com.example.springbootproject.dto.RestResponse;
import com.example.springbootproject.dto.UserInfo;
import com.example.springbootproject.domain.AppUser;
import com.example.springbootproject.domain.SecurityUserDetails;
import com.example.springbootproject.service.OptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@Slf4j
public class SignUpController {

    @Autowired
    private UserDetailsManager userDetailsManager;

//    @Autowired
//    public BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private OptService optService;

    @PostMapping(value = "/signup")
    public ResponseEntity<?> signup(@Valid UserInfo userInfo) {

        log.info("Welcome to Myapp sign up ...");
        log.info("username: {}, password: {}", userInfo.getUsername(), userInfo.getPassword());
        log.info("userInfo: {}", userInfo);

        AppUser appUser = AppUser.builder()
                .username(userInfo.getUsername())
//                .password(bCryptPasswordEncoder.encode(userInfo.getPassword()))
                .authorities(List.of("READ", "WRITE", "DELETE"))
                .mfa(userInfo.isMfa())
                .build();

        log.info("Building UserDetails");
        UserDetails user = new SecurityUserDetails(appUser);


        log.info("Creating user");
        userDetailsManager.createUser(user);

        user = userDetailsManager.loadUserByUsername(user.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Successful login for username: {}", user.getUsername());

        UserInfo.UserInfoBuilder userInfoBuilder = UserInfo.builder()
                .email(userInfo.getEmail())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .username(user.getUsername())
                .mfa(userInfo.isMfa());

        if (userInfo.isMfa()) {
            SecurityUserDetails securityUserDetails = (SecurityUserDetails) user;
            String secretKey = securityUserDetails.getOptSecretKey();

            String qrURI = optService.getOptQrCode(user.getUsername(), secretKey);
            List<String> recoveryCodes = optService.getRecoveryCodesForUser(securityUserDetails.getUserId());

            userInfoBuilder.optSecretKey(secretKey)
                    .optRecoveryCodes(recoveryCodes)
                    .qrURI(qrURI);
        }


//        RestResponse restResponse = RestResponse.builder()
//                .timestamp(Instant.now().toString())
//                .status(HttpStatus.OK.toString())
//                .username(authentication.getName())
//                .authorities(authentication.getAuthorities().toString())
//                .data(data)
//                .build();

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/whoami")
//                .path("/{id}")
//                .buildAndExpand(myUser.getId())
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(userInfoBuilder.build());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestErrorResponse handleSignUpValidationException(BindException ex) {

        log.info("Handle Sign-up Validation Exception {}", ex.getClass().toString());

        // all errors per field
        Map<String, String> errorsMap = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(groupingBy(FieldError::getField,
                        collectingAndThen(toList(), list -> list.stream()
                                .map(FieldError::getDefaultMessage).collect(Collectors.joining(",")))));

        // first error only for each field
        Map<String, String> errorsMap2 = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(groupingBy(FieldError::getField,
                        collectingAndThen(toList(), list -> list.get(0).getDefaultMessage())));

        return RestErrorResponse.builder()
                .validation(errorsMap)
                .timestamp(Instant.now().toString())
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @PostMapping(value = "/change-pwd")
    public ResponseEntity<RestResponse> changePassword(Authentication authentication,
                                                       @RequestParam("oldPassword") String oldPassword,
                                                       @RequestParam("newPassword") String newPassword) {

        log.info("Welcome to Myapp change password ...");
        log.info("Change password for user {}, old pwd: {}, new pwd: {}", authentication.getName(), oldPassword, newPassword);

        userDetailsManager.changePassword(oldPassword, newPassword);

        RestResponse restResponse = RestResponse.builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.OK.toString())
                .username(authentication.getName())
                .message("Password change.")
                .build();

        return ResponseEntity.ok()
                .body(restResponse);

    }
}
