package com.capstone.backend.domain.user.repository;

import com.capstone.backend.domain.user.entity.SocialType;
import com.capstone.backend.domain.user.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @NotNull Optional<User> findById(@NotNull Long id); // userId로 사용자 찾기
    Optional<User> findByEmail(String email); // 이메일로 사용자 찾기
    Optional<User> findByRefreshToken(String refreshToken); // 리프레시 토큰으로 사용자 찾기
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
//    Optional<User> findByName(String name); // 이름으로 사용자 찾기
//    Optional<User> findBySocialId(String socialId); // 소셜 정보로 사용자 찾기

}