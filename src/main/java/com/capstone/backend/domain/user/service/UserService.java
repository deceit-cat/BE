package com.capstone.backend.domain.user.service;

import com.capstone.backend.domain.user.entity.Role;
import com.capstone.backend.domain.user.entity.User;
import com.capstone.backend.domain.user.dto.UserSignUpDto;
import com.capstone.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signUp(UserSignUpDto userSignUpDto) throws Exception {
        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .name(userSignUpDto.getName())
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .role(Role.GUEST)
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }

    public void addInfo(String email, String socialId, Role role) throws Exception {
        Optional<User> optionalUser;
        if (socialId == null) { // email 값으로 유저 식별
            optionalUser = userRepository.findByEmail(email);
        } else { // socialId 값으로 유저 식별
            optionalUser = userRepository.findBySocialId(socialId);
        }

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (role.equals(Role.PARENT) || role.equals(Role.TEACHER)) {
                user.setRole(role);
                userRepository.save(user);
            } else {
                throw new Exception("이미 유저 구분이 설정되었습니다.");
            }
        } else {
            throw new Exception("해당 이메일을 가진 사용자를 찾을 수 없습니다.");
        }
    }
}