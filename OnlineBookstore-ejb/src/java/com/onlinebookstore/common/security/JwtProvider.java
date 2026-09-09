package com.onlinebookstore.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtProvider {
    
    // Secret key for HMAC-SHA (Minimum 256-bits / 32 characters)
    private static final String SECRET_STRING = "OnlineBookstoreSecretKeyForJwtAuthenticationTokensMinimum256BitsLong!";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    
    // Expiration time: 24 hours in milliseconds
    private static final long EXPIRATION_TIME_MS = 24 * 60 * 60 * 1000L;

    private JwtProvider() {
    }

    public static String generateToken(Integer userId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME_MS);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public static Integer getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Integer.class);
    }

    public static String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
