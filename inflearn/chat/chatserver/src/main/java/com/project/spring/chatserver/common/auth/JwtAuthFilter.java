package com.project.spring.chatserver.common.auth;

import com.project.spring.chatserver.common.dto.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String token = httpRequest.getHeader("Authorization");
        try {
            if (token != null) {
                if(!token.startsWith("Bearer ")) {
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);

                Key key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey),
                        SignatureAlgorithm.HS512.getJcaName());
//            토큰 검증 및 Claims 추출
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();
//            Authentication 객체 생성
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_"+claims.get("role")));
//                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                CustomUser customUser = CustomUser.builder()
                        .id(claims.get("id", Long.class))
                        .username(claims.getSubject())
                        .authorities(authorities)
                        .build();

                Authentication authentication = new UsernamePasswordAuthenticationToken(customUser, "", customUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }


        }catch (Exception e) {
            e.printStackTrace();
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("invalid token");
        }
        filterChain.doFilter(request, response);
    }
}
