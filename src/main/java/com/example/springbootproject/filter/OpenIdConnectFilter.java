package com.example.springbootproject.filter;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.example.springbootproject.security.OpenIdConnectUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

@Slf4j
public class OpenIdConnectFilter extends OAuth2ClientAuthenticationProcessingFilter {

//    @Value("${google.clientId}")
//    private String clientId;
//
//    @Value("${google.issuer}")
//    private String issuer;
//
//    @Value("${google.jwkUrl}")
//    private String jwkUrl;

    private OAuth2RestTemplate restTemplate;

    public OpenIdConnectFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);

        log.info("Initializing OpenIdConnectFilter with url: {}", defaultFilterProcessesUrl);
        setAuthenticationManager(new NoopAuthenticationManager());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        log.info("inside attemptAuthentication ...");

        OAuth2AccessToken accessToken = null;
        try {

            log.info("Getting Access token ....");
            accessToken = restTemplate.getAccessToken();
            log.info("Received Access token: {}", accessToken);

        } catch (OAuth2Exception e) {
            log.error("OAuth2Exception: with error code: {} and message: {} and Summery: {}",
                    e.getOAuth2ErrorCode(), e.getMessage(), e.getSummary());
            throw new BadCredentialsException("Could not obtain access token", e);
        } catch (Exception ex){
            log.error("Exception: ", ex);
        }

/*
        try {
            String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
            log.info("Received Id token: {}", idToken);

            String kid = JwtHelper.headers(idToken).get("kid");
            log.info("kid = {}", kid);

            Jwt tokenDecoded = JwtHelper.decodeAndVerify(idToken, verifier(kid));
            Map<String, String> authInfo = new ObjectMapper()
                    .readValue(tokenDecoded.getClaims(), Map.class);

            log.info("verifyClaims ... ");
            verifyClaims(authInfo);


            OpenIdConnectUserDetails user = new OpenIdConnectUserDetails(authInfo, accessToken);
            log.info("user created ... ");


//            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            return auth;
        } catch (InvalidTokenException e) {
            throw new BadCredentialsException("Could not obtain user details from token", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
*/

        return null;

    }

    private RsaVerifier verifier(String kid) throws Exception {
//        JwkProvider provider = new UrlJwkProvider(new URL(jwkUrl));
//        Jwk jwk = provider.get(kid);
//        return new RsaVerifier((RSAPublicKey) jwk.getPublicKey());

        return null;
    }

    public void verifyClaims(Map claims) {
        int exp = (int) claims.get("exp");
        Date expireDate = new Date(exp * 1000L);
        Date now = new Date();
//        if (expireDate.before(now) || !claims.get("iss").equals(issuer) ||
//                !claims.get("aud").equals(clientId)) {
//            throw new RuntimeException("Invalid claims");
//        }
    }

    public void setRestTemplate(OAuth2RestTemplate googleOpenIdTemplate) {
        restTemplate = googleOpenIdTemplate;
    }

    private static  class NoopAuthenticationManager implements AuthenticationManager {
        @Override
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
        }
    }
}