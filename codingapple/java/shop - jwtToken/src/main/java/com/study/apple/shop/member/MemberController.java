package com.study.apple.shop.member;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("member", new Member());
        return "login";
    }
    @PostMapping("/login/jwt")
    @ResponseBody
    public String loginJWT(@RequestBody Map<String, String> data, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(data.get("username"), data.get("password"));
        // 아이디 비밀번호 검증
        Authentication auth = authenticationManagerBuilder.getObject().authenticate(authToken);

        // 검증된 유저 Authentication authentication 에 추가 컨트롤러에서 꺼내쓸 수 있음
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtUtil.createToken(SecurityContextHolder.getContext().getAuthentication());

        Cookie cookie = new Cookie("JWT", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);

        String refreshToken = JwtUtil.createRefreshToken(data.get("username"));

        // 4. Refresh Token DB 저장 or 캐싱 (Rotation을 위해서 '현재 유효한 토큰 목록'을 관리)
        refreshTokenRepository.save(
                RefreshToken.createRefreshToken(data.get("username"),refreshToken)
        );

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7일 예시
        response.addCookie(refreshCookie);


        return jwt;
    }
    @PostMapping("/refresh")
    @ResponseBody
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response,
                                     Authentication auth) {
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("REFRESH_TOKEN"))
                .map(Cookie::getValue)
                .toString();
        // 2. Refresh Token의 서명 검증, 만료시간 검증
        Claims claims = JwtUtil.extractToken(refreshToken);
        // 3. DB에 있는지(유효한지) 확인
        Optional<RefreshToken> saved = refreshTokenRepository.findByUsername((String) claims.get("username"));
        if (saved.isEmpty()) {
            // 이미 rotation으로 새로운 토큰을 발급받았거나, 탈취된 토큰 등
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("만료된 리프레시 토큰");
        }
        // 4. Access Token 재발급
        String AccessToken = JwtUtil.createToken(auth);
        // 5. Refresh Token 재발급 (Rotation)
        String newRefreshToken = JwtUtil.createRefreshToken(saved.get().getUsername());

        // 6. 기존 Refresh Token 삭제 -> 새 Refresh Token DB 저장
        refreshTokenRepository.delete(saved.get());

        refreshTokenRepository.save(RefreshToken.createRefreshToken(saved.get().getUsername(),newRefreshToken));
        // 7. 쿠키에 담아서 클라이언트에 전달
        Cookie jwtCookie = new Cookie("JWT", AccessToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);

        Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        // (혹은 Body로 내려줘도 됨)
        return ResponseEntity.ok("Tokens refreshed successfully");


    }

    @GetMapping("/my-page/jwt")
    @ResponseBody
    public String myPageJWT(Authentication auth) {

        CustomUser customUser = (CustomUser) auth.getPrincipal();
        System.out.println("customUser = " + customUser);
        System.out.println("displayname = " + customUser.getDisplayName());
        System.out.println("authorities" + customUser.getAuthorities());
        return "jwt";
    }


    @GetMapping("/sign-up")
    public String signUp(Model model, Authentication auth) {

        if (Optional.ofNullable(auth).map(Authentication::isAuthenticated).orElse(false)) {
            return "redirect:/list";
        }
        model.addAttribute("member", new Member());
        return "sign-up";
    }
    @PostMapping("/sign-up")
    public String signUp(@ModelAttribute Member member) {
        String encode = passwordEncoder.encode(member.getPassword());
        member.setPassword(encode);
        memberService.save(member);
        return "redirect:/list";
    }

    @GetMapping("/my-page")
    public String myPage(Authentication auth) {
        CustomUser principal = (CustomUser) auth.getPrincipal();
        return "mypage";
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public MemberDTO getUser(@PathVariable Long id) {
        return memberService.findById(id);
    }

}
