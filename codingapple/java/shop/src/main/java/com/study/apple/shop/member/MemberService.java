package com.study.apple.shop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    public void save(Member member) {
        memberRepository.save(member);
    }

    public MemberDTO findById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return MemberDTO.builder()
                .username(member.getUsername())
                .displayName(member.getDisplayName())
                .build();
    }
}
