package com.study.apple.shop.item;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
    private Long id;
    private String title;
    private Long price;
    private String username;
    private String img;
}
