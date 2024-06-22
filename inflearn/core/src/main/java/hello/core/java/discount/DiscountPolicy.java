package hello.core.java.discount;


import hello.core.java.member.Member;

public interface DiscountPolicy {

    /*
    return 할인 금액
     */
    int discount(Member member, int price);

}
