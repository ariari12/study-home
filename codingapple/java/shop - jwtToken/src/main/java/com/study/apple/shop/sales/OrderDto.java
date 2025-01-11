package com.study.apple.shop.sales;

import lombok.*;

@Data
public class OrderDto {
    private String title;
    private Integer price;
    private Integer count;
    private Long memberId;
}
