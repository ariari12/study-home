package com.study.apple.shop.member;

import com.study.apple.shop.sales.Sales;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String displayName;
    @ToString.Exclude
    @OneToMany(mappedBy = "member")
    private List<Sales> salesList = new ArrayList<>();
}
