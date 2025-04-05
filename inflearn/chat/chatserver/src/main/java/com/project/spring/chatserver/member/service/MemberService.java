package com.project.spring.chatserver.member.service;

import com.project.spring.chatserver.member.domain.Member;
import com.project.spring.chatserver.member.dto.MemberListResDto;
import com.project.spring.chatserver.member.dto.MemberLoginReqDto;
import com.project.spring.chatserver.member.dto.MemberSaveReqDto;
import com.project.spring.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member create(MemberSaveReqDto memberSaveReqDto) {
        log.info("Create member");
//        이미 가입되어있는 이메일 검증
        if(memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }
        Member newMember = Member.builder()
                .name(memberSaveReqDto.getName())
                .email(memberSaveReqDto.getEmail())
                .password(passwordEncoder.encode(memberSaveReqDto.getPassword()))
                .build();
        log.info("생성 완료 {}", newMember);
        return memberRepository.save(newMember);
    }

    public Member login(MemberLoginReqDto memberLoginReqDto) {
        Member member = memberRepository.findByEmail(memberLoginReqDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일 입니다."));
        if(!passwordEncoder.matches(memberLoginReqDto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public List<MemberListResDto> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(Member::toMemberListDto).toList();
    }
}
