package com.example.demo.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
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

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired:" + e.getMessage());
        } catch (Exception e){
            System.out.println("Invalid JWT: " + e.getMessage());
        }

        return false;
    }

    public String extractUsername(String token){
        Claims claims = extractAllClaims(token);

        return (String) claims.get("username");
    }

    public String extractUserId(String token){
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}