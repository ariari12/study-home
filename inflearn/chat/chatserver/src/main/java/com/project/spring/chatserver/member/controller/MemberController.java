package com.project.spring.chatserver.member.controller;

import com.project.spring.chatserver.common.auth.JwtTokenProvider;
import com.project.spring.chatserver.member.domain.Member;
import com.project.spring.chatserver.member.dto.MemberListResDto;
import com.project.spring.chatserver.member.dto.MemberLoginReqDto;
import com.project.spring.chatserver.member.dto.MemberSaveReqDto;
import com.project.spring.chatserver.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto) {
        log.info("memberCreate");
        Member member = memberService.create(memberSaveReqDto);
        return ResponseEntity.ok().body(member.getId());
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginReqDto memberLoginReqDto){
        //email, password 검증
        Member member = memberService.login(memberLoginReqDto);

        // 일치할 경우 엑세스 토큰 발행
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);

    }

    @GetMapping("/list")
    public ResponseEntity<?> memberList(){
        List<MemberListResDto> dtos = memberService.findAll();
        return ResponseEntity.ok().body(dtos);
    }
}
