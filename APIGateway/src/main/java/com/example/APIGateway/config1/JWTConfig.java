package com.example.APIGateway.config1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JWTConfig {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey";

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        System.out.println("🔐 SECRET USED (Gateway): " + SECRET);

        byte[] keyBytes = SECRET.getBytes();
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withSecretKey(key).build();

        return token -> {
            System.out.println("📥 TOKEN RECEIVED IN GATEWAY: " + token);

            return decoder.decode(token)
                    .doOnNext(jwt -> {
                        System.out.println("✅ JWT VALIDATED");
                        System.out.println("📦 SUBJECT: " + jwt.getSubject());
                        System.out.println("⏰ EXPIRES AT: " + jwt.getExpiresAt());
                    })
                    .doOnError(error -> {
                        System.out.println("❌ JWT VALIDATION FAILED: " + error.getMessage());
                    });
        };
    }
}