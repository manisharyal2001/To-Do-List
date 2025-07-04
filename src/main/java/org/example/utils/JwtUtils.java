package org.example.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Arrays;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private final Key key;
    private final long expiryMillis;

    public JwtUtils(String secret, long expiryMinutes) {
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) { // 256 bits
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiryMillis = expiryMinutes * 60 * 1000;
    }

    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMillis))
                .signWith(key)
                .compact();
    }

    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    // Static helper used by TaskRoutes to decode subject (email) with provided secret
    public static String getEmailFromToken(String token, String secret) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}
