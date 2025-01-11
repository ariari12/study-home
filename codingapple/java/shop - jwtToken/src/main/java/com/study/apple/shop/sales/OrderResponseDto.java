package com.study.apple.shop.sales;

import lombok.Data;

@Data
public class OrderResponseDto {
    private String title;
    private Integer price;
    private Integer count;
    private String displayName;

    public static OrderResponseDto createOrderResponse(Sales sales) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.title = sales.getItemName();
        dto.price = sales.getPrice();
        dto.count = sales.getQuantity();
        dto.displayName = sales.getMember().getDisplayName();
        return dto;
    }
}
