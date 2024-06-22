package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class RateDiscountPolicyTest {
    @Test
    @DisplayName("VIP 10%할인")
    public static void main(String[] args) {
        DiscountPolicy discountPolicy = new RateDiscountPolicy();
        Long memberId = 1L;
        Member member = new Member(memberId, "김아현", Grade.VIP);
        int discount = discountPolicy.discount(member, 10000);
        assertThat(discount).isEqualTo(1000);

        Member member1 = new Member(memberId, "김아현", Grade.Basic);
        int discount1 = discountPolicy.discount(member1, 10000);
        assertThat(discount1).isEqualTo(0);

    }

}