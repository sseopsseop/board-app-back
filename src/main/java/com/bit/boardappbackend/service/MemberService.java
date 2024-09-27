package com.bit.boardappbackend.service;

import com.bit.boardappbackend.dto.MemberDto;

import java.util.Map;

public interface MemberService {
    Map<String, String> usernameCheck(String username);

    Map<String, String> nicknameCheck(String nickname);

    MemberDto join(MemberDto memberDto);

    MemberDto login(MemberDto memberDto);
}
