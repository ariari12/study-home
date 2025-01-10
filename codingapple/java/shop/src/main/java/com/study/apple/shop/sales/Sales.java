package com.study.apple.shop.sales;

import com.study.apple.shop.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;
    @CreationTimestamp
    private LocalDateTime createdAt;

    public static Sales createOrder(OrderDto orderDto, Member member) {
        // 도메인 규칙 적용
        if (orderDto.getPrice() != null && orderDto.getPrice() > 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        Sales sales = new Sales();
        sales.itemName = orderDto.getTitle();
        sales.price = orderDto.getPrice();
        sales.quantity = orderDto.getCount();
        sales.member = member;
        return sales;
    }
}
