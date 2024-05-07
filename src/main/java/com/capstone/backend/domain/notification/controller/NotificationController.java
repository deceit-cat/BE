package com.capstone.backend.domain.notification.controller;

import com.capstone.backend.domain.notification.service.NotificationService;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.repository.ParentRepository;
import com.capstone.backend.domain.user.repository.TeacherRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;

    @Operation(summary = "SSE 이벤트 구독")
    @GetMapping(value="/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        Optional<Teacher> teacherOptional = teacherRepository.findByUserId(userId);
        if (teacherOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher의 userId {" + userId + "}의 SSE 페이지가 열리지 않았습니다.");
        }
        return notificationService.subscribe(userId);
    }

    @Operation(summary = "데이터 변동 알림(부모의 친구추가 요청)")
    @PostMapping("/send-data/{teacherId}")
    public ResponseEntity<?> sendData(@PathVariable Long teacherId, @RequestBody Map<String, Long> requestBody) {
        Long parentUserId = requestBody.get("parentUserId");
        Optional<Parent> parentOptional = parentRepository.findByUserId(parentUserId);
        if (parentOptional.isPresent()) {
            Parent parent = parentOptional.get();
            String parentName = parent.getUser().getName();

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("parentName", parentName);
            eventData.put("parentId", parent.getUser().getId());

            notificationService.notify(teacherId, eventData);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
