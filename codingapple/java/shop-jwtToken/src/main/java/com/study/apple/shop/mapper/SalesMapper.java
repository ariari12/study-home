package com.study.apple.shop.mapper;

import com.study.apple.shop.sales.OrderResponseDto;
import com.study.apple.shop.sales.Sales;
import org.springframework.stereotype.Component;

@Component
public class SalesMapper {
    public OrderResponseDto toDto(Sales sales) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setCount(sales.getQuantity());
        orderResponseDto.setTitle(sales.getItemName());
        orderResponseDto.setPrice(sales.getPrice());
        orderResponseDto.setDisplayName(sales.getMember().getDisplayName());
        return orderResponseDto;
    }
}
