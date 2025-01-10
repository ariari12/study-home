package com.study.apple.shop.sales;

import com.study.apple.shop.mapper.SalesMapper;
import com.study.apple.shop.member.CustomUser;
import com.study.apple.shop.member.Member;
import com.study.apple.shop.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final SalesRepository salesRepository;
    private final MemberRepository memberRepository;
    private final SalesMapper salesMapper;

    public void save(OrderDto orderDto, Object principal) {
        CustomUser customUser = (CustomUser) principal;
        Member member = memberRepository.findById(customUser.getId()).orElseThrow(() ->
                new IllegalArgumentException("회원 정보를 찾을 수 없습니다. ID: " + customUser.getId()));
        Sales order = Sales.createOrder(orderDto, member);
        salesRepository.save(order);
    }

    public Page<OrderResponseDto> findPageByOrderByIdDesc(Pageable pageable) {
        Page<Sales> sales = salesRepository.findPageSales(pageable);
        return sales.map(salesMapper::toDto);
    }

}
