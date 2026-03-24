//package com.example.WalletService.config;
//
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class OAuthFeignInterceptor implements RequestInterceptor {
//
//    @Override
//    public void apply(RequestTemplate template) {
//        String token = "PLACEHOLDER_TOKEN"; // replace later with real token fetch
//        template.header("Authorization", "Bearer " + token);
//    }
//}