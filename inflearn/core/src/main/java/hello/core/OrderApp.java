package hello.core;

import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.member.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class OrderApp {
    public static void main(String[] args) {
//        AppConfig appConfig = new AppConfig(); 순수 자바로만 썼을때
//        MemberService memberService = appConfig.memberService();
//        OrderService orderService = appConfig.orderService();

        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = applicationContext.getBean("memberService", MemberService.class);
        OrderService orderService = applicationContext.getBean("orderService", OrderService.class);

        Long memberId = 1L;
        Member member = new Member(memberId, "김아현", Grade.VIP);
        memberService.join(member);

        Order order = orderService.createOrder(member.getId(), "itemA", 10000);
        System.out.println(order);

    }

}
