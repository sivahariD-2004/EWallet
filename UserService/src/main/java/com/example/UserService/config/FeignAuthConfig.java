package com.example.UserService.Config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignAuthConfig {

    @Bean
    public RequestInterceptor bearerForwardingInterceptor() {
        return template -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                template.header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + jwtAuth.getToken().getTokenValue()
                );
            }
        };
    }
}