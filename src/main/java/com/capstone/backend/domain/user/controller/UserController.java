package com.capstone.backend.domain.user.controller;

import com.capstone.backend.domain.chat.service.ChatRoomService;
import com.capstone.backend.domain.user.dto.*;
import com.capstone.backend.domain.user.entity.Role;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.entity.User;
import com.capstone.backend.domain.user.service.UserService;
import com.capstone.backend.global.jwt.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "유저 관리", description = "회원가입,로그인, 추가정보 입력, 로그인 통계 조회, 전체 사용자 수 조회")
public class UserController {
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final JwtService jwtService;

    @Operation(summary = "회원가입")
    @PostMapping("/auth/sign-up")
    public ResponseEntity<String> singUp(@RequestBody UserDto userDto) {
        try {
            userService.signUp(userDto);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("해당 사용자가 이미 존재합니다.");
        }
    }

    /**
     * 사용자 로그인을 처리
     *
     * @param userDto 사용자 로그인 정보를 담고 있는 DTO
     * @return 로그인 성공 여부 및 관련 정보 ( access token, role )
     */
    @Operation(summary = "로그인")
    @PostMapping("/auth/sign-in")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDto userDto) {
        Map<String, Object> tokens = userService.loginUser(userDto.getEmail(), userDto.getPassword());
        Role role = userService.getUserRole(userDto.getEmail());
        Long userId = userService.getUserId(userDto.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, (String) tokens.get("accessToken"));

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("role", role);
        responseBody.put("userId", userId);

        return ResponseEntity.ok().headers(headers).body(responseBody);
    }

    @Operation(summary = "추가 정보 입력")
    @PostMapping("/auth/add-info")
    public ResponseEntity<String> addInfo(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token) {
        try {
            userService.addInfo(userDto, token);
            return ResponseEntity.ok("사용자의 추가정보 입력 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "학부모 토큰으로 선생님 찾기")
    @GetMapping("/findTeachers")
    public ResponseEntity<List<TeacherDto>> findTeachers(@RequestHeader("Authorization") String token) {
        try {
            List<TeacherDto> teachers = userService.findTeachers(token);
            return ResponseEntity.ok(teachers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 전체 유저의 통합 주간 로그인 횟수 조회
     * @return
     */
    @Operation(summary = "전체 유저의 통합 주간 로그인 횟수 조회")
    @GetMapping("/getWeeklyLoginCount")
    public ResponseEntity<Long> getTotalWeeklyLoginCount() {
        return ResponseEntity.ok(userService.getTotalWeeklyLoginCount());
    }

    /**
     * 시스템에 등록된 전체 유저 수 조회
     * @return
     */
    @Operation(summary = "시스템에 등록된 전체 유저 수 조회")
    @GetMapping("/getTotalUserCount")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.getUserCount());
    }

    @Operation(summary = "근무 상태 설정")
    @PostMapping("/status/{teacherUserId}")
    public ResponseEntity<String> setWorkStatus(@PathVariable Long teacherUserId, @RequestBody TeacherStatusDto teacherStatusDto) {
        try {
            userService.setWorkStatus(teacherUserId, teacherStatusDto);
            return ResponseEntity.ok("상태가 설정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "근무 상태 조회")
    @GetMapping("/status/{teacherUserId}")
    public ResponseEntity<TeacherStatusDto> getWorkStatus(@PathVariable Long teacherUserId) {
        try {
            TeacherStatusDto teacherStatusDto = userService.getWorkStatus(teacherUserId);
            return ResponseEntity.ok(teacherStatusDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "유저 정보 요청")
    @GetMapping("/entry")
    public ResponseEntity<?> getUserInfo(@RequestHeader(name="Authorization") String token) {
        try {
            User user = userService.validateAccessTokenAndGetUser(token);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole().toString());

            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
    }
}