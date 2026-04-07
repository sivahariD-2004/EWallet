package com.example.UserService.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey";

    private Key getSigningKey(){
        System.out.println(" SECRET USED (UserService): " + SECRET);
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateToken(String email){

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+86400000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        System.out.println(" GENERATED TOKEN: " + token);

        return token;
    }
}