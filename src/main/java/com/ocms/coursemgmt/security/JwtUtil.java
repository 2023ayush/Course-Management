package com.ocms.coursemgmt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSecretKey())
                .compact();
    }


    public String extractEmail(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String extractRole(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

        return claims.get("role", String.class);
    }


    public boolean validateToken(String token, String email){
        try {
            String tokenEmail = extractEmail(token);
            String tokenRole = extractRole(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e){
            return false;
        }
    }

    private boolean isTokenExpired(String token){
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiration.before(new Date());
    }
}