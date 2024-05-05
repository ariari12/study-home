package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){ //DTO로 감싼 join 조회
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v3/orders") //컬렉션 최적화 fetch join 조회
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(order -> new OrderDto(order))
                .collect(Collectors.toList());

    }

    @GetMapping("/api/v3.1/orders") //컬렉션 batch size 조회 이건 V3와 달리 페이징 가능
    public List<OrderDto> orderV3_page(@RequestParam("offset")int offset,
                                       @RequestParam("limit")int limit){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); //페이징 처리하면서 Member와 Delivery 페치조인으로 가져옴
        return orders.stream()
                .map(o-> new OrderDto(o))// 여기서 in 쿼리로 아이템들을 가져와서 리스트로 조립함
                .collect(Collectors.toList());

    }

    @GetMapping("/api/v4/orders") //DTO직접 조회 쿼리 3번 주문 하나 조회할때 성능이 나옴
    public List<OrderQueryDto> orderV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders") //DTO직접 조회 컬렉션 최적화 쿼리 2번
    public List<OrderQueryDto> orderV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }


    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private Address address;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private List<OrderItemDto> orderItems;


        public OrderDto(Order order) {
            orderId =order.getId();
            name=order.getMember().getName();
            orderDate=order.getOrderDate();
            orderStatus=order.getStatus();
            address=order.getDelivery().getAddress();
            orderItems=order.getOrderItems()
                    .stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());

        }


    }
    @Getter
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;
        public OrderItemDto(OrderItem orderItem) {
            itemName=orderItem.getItem().getName();
            orderPrice=orderItem.getOrderPrice();
            count=orderItem.getCount();

        }
    }
}
