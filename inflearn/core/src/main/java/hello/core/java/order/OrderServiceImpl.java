package hello.core.java.order;

import hello.core.java.discount.DiscountPolicy;
import hello.core.java.discount.FixDiscountPolicy;
import hello.core.java.member.Member;
import hello.core.java.member.MemberRepository;
import hello.core.java.member.MemoryMemberRepository;

public class OrderServiceImpl implements OrderService {
    private final MemberRepository memberRepository = new MemoryMemberRepository();
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member,itemPrice);
        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
