package com.study.apple.shop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("member", new Member());
        return "login";
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
