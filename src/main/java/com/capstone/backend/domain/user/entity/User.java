package com.capstone.backend.domain.user.entity;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.persistence.*;

@Table(name = "USERS")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role = Role.GUEST;

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType; // GOOGLE, NAVER, KAKAO

    private String socialId; // 로그인 한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

//    public int getUserCount() {
//        return users.size();
//    }
}