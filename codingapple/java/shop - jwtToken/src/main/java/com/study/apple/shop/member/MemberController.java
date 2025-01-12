package com.study.apple.shop.member;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
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


        return jwt;
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
