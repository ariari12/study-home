package com.study.apple.shop.member;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtCookie = "";
        for(Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT")) {
                jwtCookie = cookie.getValue();
                System.out.println("jwtCookie = " + jwtCookie);
            }
        }
        System.out.println("jwtCookie = " + jwtCookie);
        Claims claims;
        try{
            claims = JwtUtil.extractToken(jwtCookie);

        }catch (Exception e){
            filterChain.doFilter(request, response);
            return;
        }

        String[] arr = claims.get("authorities").toString().split(",");
        var authorities =
                Arrays.stream(arr).map(SimpleGrantedAuthority::new).toList();

        CustomUser customUser = new CustomUser(
                claims.get("username").toString(),
                "none", // 비밀번호자리이나 토큰정보에는 패스워드를 넣지않았으므로 null값
                authorities
        );
        customUser.setDisplayName(claims.get("displayName").toString());

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                // 유저네임, 패스워드
                customUser,
                null,
                authorities
        );
        authToken.setDetails(new WebAuthenticationDetailsSource()
                .buildDetails(request));

        //jwt는 이미 검증된 작업으로 authenticationManagerBuilder.getObject().authenticate(authToken); 생략 가능
        SecurityContextHolder.getContext().setAuthentication(authToken);
        System.out.println("auth = " + SecurityContextHolder.getContext().getAuthentication());

        //실행할 코드
        filterChain.doFilter(request, response);

    }
}
