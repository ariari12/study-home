package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderQueryDto {
    private Long orderId;
    private String name;
    private Address address;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private List<OrderItemQueryDto>orderItems;

    public OrderQueryDto(Long orderId, String name, Address address, LocalDateTime orderDate, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.name = name;
        this.address = address;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }
}
