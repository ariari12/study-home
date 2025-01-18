package com.study.apple.shop.sales;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SalesController {
    private final SalesService salesService;

    @GetMapping("/order/{id}")
    public String order(Model model, @PathVariable Integer id) {
        // 주문내역 조회
        Page<OrderResponseDto> result = salesService.findPageByOrderByIdDesc(PageRequest.of(id, 10));
        model.addAttribute("sales", result);
        return "sales";
    }

    @PostMapping("/order")
    public String order(@ModelAttribute OrderDto orderDto, Authentication authentication) {
         salesService.save(orderDto, authentication.getPrincipal());
        return "list";
    }
}
