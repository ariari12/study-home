package com.study.apple.shop.member;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    static final SecretKey key =
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                    "jwtpassword123jwtpassword123jwtpassword123jwtpassword123"
            ));

    // JWT 만들어주는 함수
    public static String createToken(Authentication authentication) {
        CustomUser user = (CustomUser) authentication.getPrincipal();
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String jwt = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("displayName", user.getDisplayName())
                .claim("authorities", authorities)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30000)) //유효기간 30초
                .signWith(key)
                .compact();
        return jwt;
    }

    // JWT 까주는 함수
    public static Claims extractToken(String token) {
        System.out.println("token = " + token);
        Claims claims = Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();
        System.out.println("claims = " + claims);
        return claims;
    }

    // 리프레시 토큰 생성
    public static String createRefreshToken(String username) {
        long refreshTokenValidTime = 1000L * 60L * 60L * 24L * 7L; // 7일 예시

        // 여기서는 간단하게 JWT 포맷으로 Refresh Token을 만듭니다.
        return Jwts.builder()
                .subject("refresh-token")
                .claim("username", username)
                .issuedAt(new Date(System.currentTimeMillis()))
                // Refresh Token 만료시간: 7일
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidTime))
                .signWith(key)
                .compact();
    }
}
