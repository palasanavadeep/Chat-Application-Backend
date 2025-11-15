package com.navadeep.ChatApplication.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    Log log = LogFactory.getLog(JwtUtil.class);

    private String secretKey;
    private long expirationMs;

    // Spring will call these setters from XML
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody().getSubject(); // userId
        } catch (JwtException e) {
            // includes ExpiredJwtException, MalformedJwtException, etc.
            log.error("Invalid or expired JWT: " + e.getMessage(),e);
            return null;
        }
    }
}
