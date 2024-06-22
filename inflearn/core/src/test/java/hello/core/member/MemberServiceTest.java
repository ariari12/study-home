package hello.core.member;

import hello.core.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    AppConfig appConfig = new AppConfig();
    MemberService memberService = appConfig.memberService();

    //given
    @Test
    void join() {
        Member member = new Member(1L, "김아현", Grade.VIP);

        //when
        memberService.join(member);
        Member member1 = memberService.findMember(1L);
        //then
        Assertions.assertThat(member.equals(member1));
    }
}
