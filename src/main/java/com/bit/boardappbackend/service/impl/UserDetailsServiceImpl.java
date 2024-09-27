package com.bit.boardappbackend.service.impl;

import com.bit.boardappbackend.entity.CustomUserDetails;
import com.bit.boardappbackend.entity.Member;
import com.bit.boardappbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("member not exist")
        );

        return CustomUserDetails.builder()
                .member(member)
                .build();
    }
}
