package com.project.spring.chatserver.member.service;

import com.project.spring.chatserver.member.domain.Member;
import com.project.spring.chatserver.member.dto.MemberSaveReqDto;
import com.project.spring.chatserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member create(MemberSaveReqDto memberSaveReqDto) {
//        이미 가입되어있는 이메일 검증
        if(memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }
        Member newMember = Member.builder()
                .name(memberSaveReqDto.getName())
                .email(memberSaveReqDto.getEmail())
                .password(memberSaveReqDto.getPassword())
                .build();
        return memberRepository.save(newMember);
    }
}
