package com.study.apple.shop.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    public static Object CustomUser;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //DB에서 username을 가진 유저를 찾아와서
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(username + "이란 아이디가 없습니다"));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //return new User(유저아이디, 비번, 권한) 해주세요
        //return new User(member.getUsername(), member.getPassword(),grantedAuthorities);
        CustomUser user = new CustomUser(member.getUsername(), member.getPassword(), grantedAuthorities);
        user.setDisplayName(member.getDisplayName());
        user.setId(member.getId());
        return user;
    }


}

