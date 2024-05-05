package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;


    @Test
    public void 상품주문() throws Exception{
        Member member = createMember();
        Book book = createBook("jpa", 1000, 10);

        int count=3;
        Long orderId = orderService.order(member.getId(), book.getId(), count);
        Order order = orderRepository.findOne(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(order.getOrderItems().size()).isEqualTo(1);
        assertThat(order.getTotalPrice()).isEqualTo(book.getPrice()*count);
        assertThat(book.getStockQuantity()).isEqualTo(7);

    }



    @Test
    public void 상품주문_재고수량초과() throws Exception{
        Member member = createMember();
        Book book = createBook("jpa2", 2000, 30);

        int count=40;

        assertThatThrownBy(() -> orderService.order(member.getId(), book.getId(), count)).isInstanceOf(NotEnoughStockException.class);

    }
    @Test
    public void 상품취소() throws Exception{
        Member member = createMember();
        Book book = createBook("jpa3", 300, 10);
        int count=5;
        Long orderId = orderService.order(member.getId(), book.getId(), count);
        Order order = orderRepository.findOne(orderId);
        assertThat(book.getStockQuantity()).isEqualTo(5);
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(10);
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("kim");
        member.setAddress(new Address("city","street","zipcode"));
        entityManager.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int quantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.addStock(quantity);
        entityManager.persist(book);
        return book;
    }

}