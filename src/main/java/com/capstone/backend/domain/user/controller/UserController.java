package com.capstone.backend.domain.user.controller;

import com.capstone.backend.domain.user.dto.UserSignUpDto;
import com.capstone.backend.domain.user.entity.Role;
import com.capstone.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/join")
    public String singUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);
        return "회원가입 성공";
    }

    @PostMapping("/auth/add-info")
    public ResponseEntity<String> setUserRole(@RequestParam(required = false) String email, @RequestParam(required = false) String socialId, @RequestParam Role role) {
        try {
            userService.addInfo(email, socialId, role);
            return ResponseEntity.ok("사용자 역할이 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/auth/profile")
    public String profile() {
        return "jwtTest 요청 성공";
    }
}
