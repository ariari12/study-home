package com.project.spring.chatserver.member.domain;

import com.project.spring.chatserver.common.domain.BaseTimeEntity;
import com.project.spring.chatserver.member.dto.MemberListResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    public MemberListResDto toMemberListDto() {
        MemberListResDto dto = new MemberListResDto();
        dto.setId(id.toString());
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}
