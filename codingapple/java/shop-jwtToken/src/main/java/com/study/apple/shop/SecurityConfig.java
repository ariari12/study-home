package com.study.apple.shop;

import com.study.apple.shop.member.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf)-> csrf.disable());
        //세션 데이터를 생성을 막는 코드(jwt 토큰을 만들 예정)
        http
                .sessionManagement((session) -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .addFilterBefore(new JwtFilter(), ExceptionTranslationFilter.class);
        http
                .authorizeHttpRequests(authorize ->
                    authorize
                        .requestMatchers("/**").permitAll()
        )
//            .formLogin(formLogin -> formLogin.loginPage("/login")
//                .defaultSuccessUrl("/")
//        )
        ;
        return http.build();
    }
}
