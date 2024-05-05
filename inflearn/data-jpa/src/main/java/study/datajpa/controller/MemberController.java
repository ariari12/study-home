package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;
    @GetMapping("members/{id}")
    public String findMember(@PathVariable("id")Long id){
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }
    @GetMapping("members2/{id}") //도메인 클래스 컨버터 권장은 안함
    public String findMember(@PathVariable("id")Member member){
        return member.getUsername();
    }
    @GetMapping("/members")
    public Page<Member> listV1(@PageableDefault(size = 5,sort = "username") Pageable pageable){
        return memberRepository.findAll(pageable);
    }
    @GetMapping("/membersV2")
    public Page<MemberDto> listV2(@PageableDefault(size = 5) Pageable pageable){
        Page<Member> page = memberRepository.findAll(pageable);
        return page.map(MemberDto::new);
    }

    //@PostConstruct
    public void init(){
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user"+i,i));
        }

    }
}
