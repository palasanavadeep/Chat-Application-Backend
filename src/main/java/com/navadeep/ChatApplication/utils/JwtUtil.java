//package com.navadeep.ChatApplication.utils;
//
//import io.jsonwebtoken.*;
//import java.util.Date;
//
//public class JwtUtil {
//    private String secret = "super_secret_key";
//    private long validity = 86400000; // 1 day
//
//    public String generateToken(Long userId, String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("userId", userId)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + validity))
//                .signWith(SignatureAlgorithm.HS256, secret)
//                .compact();
//    }
//
//    public Claims validateToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(secret)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}