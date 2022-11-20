package com.example.springbootproject.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@Slf4j
public class CsrfTokenLogger implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        var httpRequest = (HttpServletRequest) request;

        Object o = request.getAttribute("_csrf");
        CsrfToken csrfToken = (CsrfToken) o;
        log.info("For path: {}, CSRF token: {}, CSRF ParameterName: {}, CSRF HeaderName: {}",
                httpRequest.getServletPath(),
                csrfToken.getToken(), csrfToken.getParameterName(), csrfToken.getHeaderName());

//        Enumeration<String> attributes = request.getAttributeNames();
//        attributes.asIterator().forEachRemaining(attr -> {
//            log.info("{}: {}", attr, request.getAttribute(attr));
//        });

        filterChain.doFilter(request, response);
    }

}