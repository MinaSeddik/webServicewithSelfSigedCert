package com.example.springbootproject.controller;

import com.example.springbootproject.service.MyRepoService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@Slf4j
@Timed
public class MyRepoController {
    @Autowired
    public MyRepoService myRepoService;

    @Autowired
    private WebClient webClient;

    @Timed(value = "my-app.myrepo.timed")
    @RequestMapping(value = "/repo")
    public Map<String, String> getRepo(HttpServletRequest request, Authentication authentication) {

        log.info("this is info log");
        log.trace("this is trace log");
        log.debug("this is debug log");

        log.info("--------> Here in MainController ... {}", request);
        log.info("--------> Here in MainController ... {}", authentication);

        String authorization = request.getHeader("Authorization");
        log.info("authorization: {}", authorization);

        Map<String, String> data = myRepoService.getAllItems();
        data.put("Authorization-Header", authorization);

//        SecurityContext context = SecurityContextHolder.getContext();
//        Authentication authentication = context.getAuthentication();
        if (Objects.nonNull(authentication)) {
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities().toString());
        }

        return data;
    }

    @Timed(value = "my-app.myrepo.timed")
    @RequestMapping(value = "/repor")   // repo-reactive
    public Map<String, String> getRepo(ServerHttpRequest request,
                                       OAuth2AuthenticationToken authentication) { // or ServerWebExchange

        log.info("this is info log");
        log.trace("this is trace log");
        log.debug("this is debug log");

        log.info("--------> Here in MainController ... {}", request);
        log.info("--------> Here in MainController ... {}", authentication);

        String authorization = request.getHeaders().getFirst("Authorization");
        log.info("authorization: {}", authorization);

        Map<String, String> data = myRepoService.getAllItems();
        data.put("Authorization-Header", authorization);

        if (Objects.nonNull(authentication)) {
            data.put("username", authentication.getName());
            data.put("username2", authentication.getPrincipal().getName());
            data.put("authorities", authentication.getAuthorities().toString());

            OAuth2User oAuth2User = authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();

            data.put("google_name", attributes.get("name").toString());
            data.put("given_name", attributes.get("given_name").toString());
            data.put("family_name", attributes.get("family_name").toString());

        }

        return data;
    }

    @DeleteMapping(value = "/repo")
    public Map<String, String> deleteRepo(Authentication authentication) {

        log.info("Inside delete repo ...");

        Map<String, String> data = new HashMap<>();
        data.put("repo", "repo-name");
        data.put("status", "deleted");

        if (Objects.nonNull(authentication)) {
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities().toString());
        }

        return data;
    }

    @PostMapping(value = "/repo")
    @CrossOrigin("http://localhost:8080")
    public Map<String, String> postRepo(Authentication authentication) {

        log.info("Inside post repo ...");

        Map<String, String> data = new HashMap<>();
        data.put("repo", "repo-name");
        data.put("status", "deleted");

        if (Objects.nonNull(authentication)) {
            data.put("username", authentication.getName());
            data.put("authorities", authentication.getAuthorities().toString());
        }

        return data;
    }

    @RequestMapping(value = "/auth")
    public ResponseEntity<String> auth() {

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Token", UUID.randomUUID().toString());

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body("Ok");
    }

    @Timed(value = "my-app.myrepo.search.timed")
    @RequestMapping(value = "/repo/search")
    public Map<String, String> searchRepo(@QueryParam("q") String value) {

        log.info("Received Search with query-param = {}", value);
        Map<String, String> data = myRepoService.getAllItems();
        return data;
    }

    @RequestMapping(value = "/trans")
    public String trans() {
        String str = myRepoService.trans1();
        return str;
    }

    @RequestMapping(value = "/trans2")
    public String trans2() {
        String str = myRepoService.trans2();
        return str;
    }

    @RequestMapping(value = "/trans3")
    public String trans3() {
        String str = myRepoService.trans3();
        return str;
    }


}
