package com.example.springbootproject.job;

import com.example.springbootproject.service.MyRepoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;


@Profile("temp")
@Component
@Slf4j
public class RecurringScheduleJob {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private MyRepoService myRepoService;

    @Autowired
    private ObjectMapper objectMapper;

    //    @Scheduled(cron = "${cron.expression}")
//    @Scheduled(cron = "0 0,15,30,45 * * * ?")
    public void scheduleTaskUsingCronExpressionEvery15Minute() {

        long now = System.currentTimeMillis() / 1000;
        log.info("schedule every 15 minutes tasks using cron jobs - {} milli, Time: {}", now, Instant.now().toString());

        try {
            loginSystemUser();

            Map<String, String> map = myRepoService.getAllItems();

            String json = objectMapper.writeValueAsString(map);
            log.info("JSON: {}", json);

        } catch (Exception ex) {
            log.error("{} ", ex.getClass(), ex);
        } finally {
            logoutSystemUser();
        }


    }

    private void loginSystemUser() {

        log.info("Log in as SystemUser");

        // setup manual login
        String systemUser = "SYSTEM_USER";
        String systemPassword = "12345";
        Authentication authentication = new UsernamePasswordAuthenticationToken(systemUser, systemPassword);
        authentication = authManager.authenticate(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

//        HttpSession session = request.getSession(true);
//        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, securityContext);
    }

    private void logoutSystemUser() {

        log.info("Logout as SystemUser");

//        HttpSession session = requset.getSession(false);
//        if(!Objects.isNull(session)) {
//            session.invalidate();
//        }

        SecurityContextHolder.clearContext();

    }


}
