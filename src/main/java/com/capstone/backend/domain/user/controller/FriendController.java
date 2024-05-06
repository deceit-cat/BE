package com.capstone.backend.domain.user.controller;

import com.capstone.backend.domain.user.dto.FriendDto;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.service.ChildService;
import com.capstone.backend.domain.user.service.FriendService;
import com.capstone.backend.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class FriendController {
    private final FriendService friendService;
    private final ChildService childService;
    private final UserService userService;

    public FriendController(FriendService friendService, ChildService childService, UserService userService) {
        this.friendService = friendService;
        this.childService = childService;
        this.userService = userService;
    }

    @Operation(summary = "친구추가 승인")
    @PostMapping("/friends/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody Map<String, Long> requestBody) {
        Long teacherUserId = requestBody.get("teacherUserId");
        Long parentUserId = requestBody.get("parentUserId");

        Teacher teacher = userService.findTeacherById(teacherUserId);
        Parent parent = userService.findParentById(parentUserId);

        if (teacher != null && parent != null) {
            if (!friendService.areFriends(teacher,parent)) {
                friendService.acceptFriendRequest(teacher, parent);
                childService.mapTeacherToChild(teacher, parent);
                String message = String.format("선생님 %s와 학부모 %s의 친구 추가 완료", teacher.getTeacherName(), parent.getUser().getName());
                return ResponseEntity.ok(message);
            } else {
                String message = String.format("선생님 %s와 학부모 %s은(는) 이미 친구 추가가 완료되었습니다.", teacher.getTeacherName(), parent.getUser().getName());
                return ResponseEntity.ok(message);
            }
        } else {
            return ResponseEntity.badRequest().body("선생님 혹은 부모를 찾을 수 없습니다.");
        }
    }

    @Operation(summary = "친구 정보 및 방 번호 찾기")
    @GetMapping("/findRoomId")
    public ResponseEntity<List<FriendDto>> getUserFriends(@RequestHeader("Authorization") String token) {
        try {
            List<FriendDto> userFriends = friendService.findUserFriends(token);
            return ResponseEntity.ok(userFriends);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "매핑된 값이 없습니다.", e);
        }
    }
}

