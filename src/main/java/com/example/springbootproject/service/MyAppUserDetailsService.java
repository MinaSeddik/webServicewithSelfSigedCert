package com.example.springbootproject.service;

import com.example.springbootproject.domain.AuthorityEntity;
import com.example.springbootproject.domain.UserEntity;
import com.example.springbootproject.exception.InValidCredentialsException;
import com.example.springbootproject.model.AppUser;
import com.example.springbootproject.model.Authority;
import com.example.springbootproject.model.EncryptionAlgorithm;
import com.example.springbootproject.model.SecurityUserDetails;
import com.example.springbootproject.repository.impl.AuthorityRepository;
import com.example.springbootproject.repository.impl.OtpRecoveryRepository;
import com.example.springbootproject.repository.impl.UserRepository;
import com.example.springbootproject.security.otp.OtpAuthenticator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyAppUserDetailsService implements UserDetailsManager {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SCryptPasswordEncoder sCryptPasswordEncoder;

    @Autowired
    private OtpAuthenticator otpAuthenticator;

    @Autowired
    private OtpRecoveryRepository otpRecoveryRepository;

    /*
        The service should throw an exception of type UsernameNotFoundException
        if the user doesn’t exist.
     */
    @Override
    public SecurityUserDetails loadUserByUsername(String username) {
        log.info("Load user by username:{}", username);

        Supplier<UsernameNotFoundException> supplier =
                () -> {
                    log.warn("Invalid Username {}; user doesn't exist", username);
//                    throw new UsernameNotFoundException("Username or Password isn't correct!");
                    throw new InValidCredentialsException(HttpStatus.UNAUTHORIZED, "Username or Password isn't correct!");
                };

        UserEntity userEntity = userRepository.findUserByUsername(username)
                .orElseThrow(supplier);

        // use model mapper instead
        List<Authority> authorities = authorityRepository.findAuthoritiesOfUser(userEntity.getId())
                .stream()
                .map(a -> Authority.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .build())
                .collect(Collectors.toList());
        log.debug("Authorities for {}: {}", username, authorities);

        // use model mapper instead
        AppUser user = AppUser.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword().trim())
                .algorithm(EncryptionAlgorithm.valueOf(userEntity.getAlgorithm()))
                .authorities(authorities.stream().map(Authority::getName).collect(Collectors.toList()))
                .mfa(userEntity.isMfa())
                .optSecretKey(userEntity.getSecretKey())
                .build();

        return new SecurityUserDetails(user);
    }

    @Override
//    @Transactional
    public void createUser(UserDetails user) {

        log.info("creating new User ...");
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) user;

        validateNewUser(securityUserDetails);

        int userId = createNewUserAndGetId(securityUserDetails);

        addAuthoritiesForUser(userId, securityUserDetails);

        addOtpRecoveryCodesIf2faEnabled(userId, securityUserDetails);

    }

    private void addOtpRecoveryCodesIf2faEnabled(int userId, SecurityUserDetails securityUserDetails) {

        if (securityUserDetails.is2faEnabled()) {
            // generate Recovery codes and associate them with the user
            List<String> recoveryCodes = otpAuthenticator.generateRecoveryCodes();
            int persisted = otpRecoveryRepository.addRecoveryCodesForUser(userId, recoveryCodes);
            log.info("RecoveryCodes for User {} persisted to the db, Recovery Codes count: {}", securityUserDetails.getUsername(), persisted);
        }
    }

    private void addAuthoritiesForUser(int userId, SecurityUserDetails securityUserDetails) {
        List<AuthorityEntity> authorityEntities = securityUserDetails.getAuthorities()
                .stream()
                .map(a -> AuthorityEntity.builder()
                        .user(userId)
                        .name(a.getAuthority())
                        .build())
                .collect(Collectors.toList());

        int persisted = authorityRepository.addAuthorities(authorityEntities);
        log.info("Authorities for User {} persisted to the db, Authorities count: {}", securityUserDetails.getUsername(), persisted);
    }

    private int createNewUserAndGetId(SecurityUserDetails securityUserDetails) {

        String secretKey = null;
        if (securityUserDetails.is2faEnabled()) {
            secretKey = otpAuthenticator.generateSecretKey();
        }

        UserEntity userEntity = UserEntity.builder()
                .username(securityUserDetails.getUsername())
                .password(securityUserDetails.getPassword())
                .mfa(securityUserDetails.is2faEnabled())
                .secretKey(secretKey)
                .build();

        log.info("Save user {}, to the db", userEntity.getUsername());

        int userId = userRepository.addUser(userEntity);
        log.info("User {} persisted to the db with Id={}", securityUserDetails.getUsername(), userId);

        return userId;
    }

    private void validateNewUser(SecurityUserDetails securityUserDetails) {
        if (Objects.isNull(securityUserDetails)) {
            log.error("UserDetails is NULL");
            throw new RuntimeException("User details are missed, Can't be created.");
        }

        log.info("Check if username exists ...");
        if (userExists(securityUserDetails.getUsername())) {
            log.error("User {} already exists", securityUserDetails.getUsername());
            throw new RuntimeException("An account with the same username: " + securityUserDetails.getUsername() + " already exists.");
        }
    }

    @Override
    public void updateUser(UserDetails user) {
        throw new UnsupportedOperationException("Action update user is NOT supported!");
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException("Action delete user is NOT supported!");
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

        SecurityContext sc = SecurityContextHolder.getContext();
        String username = sc.getAuthentication().getName();
        log.info("changePassword for {}", username);

        // check the old password is matching the pwd specified in the database
        // ....


//        return userRepository.setPassword(username)
//                .isPresent();
    }

    @Override
    public boolean userExists(String username) {

        log.info("userExists {}", username);
        return userRepository.findUserByUsername(username)
                .isPresent();

    }
}