package com.study.apple.shop.member;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MemberDTO {
    private String username;
    private String displayName;
}
