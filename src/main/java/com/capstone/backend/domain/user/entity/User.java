package com.capstone.backend.domain.user.entity;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USERS")
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column
    private String name; // 이름

    @Column(unique = true) // email 은 중복되지 않아야 하기 때문에 unique 하도록 설정
    private String email; // 이메일

    @Column
    private String password; // 비밀번호

//    @Column
//    private String imgUrl; // 프로필 이미지

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType; // GOOGLE, NAVER, KAKAO

    private String socialId; // 로그인 한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    @Column
    @Enumerated(EnumType.STRING)
    private Role role; // GUEST, PARENT, TEACHER

    public void setRole(Role role) {
        if (role == Role.PARENT) {
            this.role = Role.PARENT;
        } else if (role == Role.TEACHER) {
            this.role = Role.TEACHER;
        } else {
            this.role = Role.GUEST; // default 값
        }
    }

    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}
