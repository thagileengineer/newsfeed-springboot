package com.example.demo.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.Users;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtils {

    private final String jwtSecret;
    private final long expirationMs;

    private final SecretKey key;

    public JwtUtils(
        @Value("${jwt.secret}") String jwtSecret,
        @Value("${jwt.expiration-ms}") long expirationMs
    ){
        this.jwtSecret = jwtSecret;
        this.expirationMs = expirationMs;

        if(this.jwtSecret == null || this.jwtSecret.isEmpty()){
            throw new IllegalStateException("JWT Secret missing");
        }

        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(Users user){

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());

        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}