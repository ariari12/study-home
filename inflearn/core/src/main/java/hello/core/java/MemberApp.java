package hello.core.java;

import hello.core.AppConfig;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;

public class MemberApp {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        MemberService memberService = appConfig.memberService();
        //MemberService memberService = new MemberServiceImpl();

        Member member = new Member(1L, "김아현", Grade.VIP);

        memberService.join(member);
        Member findMember = memberService.findMember(1L);
        System.out.println("new member ="+member.getName());
        System.out.println("find member ="+findMember.getName());
    }
}
