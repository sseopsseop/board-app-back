package com.bit.boardappbackend.dto;

import com.bit.boardappbackend.entity.Member;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MemberDto {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String tel;
    private String role;
    private String token;

    public Member toEntity() {
        return Member.builder()
                .id(this.id)
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .email(this.email)
                .tel(this.tel)
                .role(this.role)
                .build();
    }













}
