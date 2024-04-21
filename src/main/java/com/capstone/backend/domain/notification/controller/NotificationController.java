package com.capstone.backend.domain.notification.controller;

import com.capstone.backend.domain.notification.service.NotificationService;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.webjars.NotFoundException;

import java.util.Optional;

@RestController
@RequestMapping("/notify")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final TeacherRepository teacherRepository;

    @GetMapping(value="/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        Optional<Teacher> teacherOptioanl = teacherRepository.findByUserId(userId);
        if (teacherOptioanl.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher의 userId {" + userId + "}의 SSE 페이지가 열리지 않았습니다.");
        }
        return notificationService.subscribe(userId);
    }

    @PostMapping("/send-data/{teacherId}")
    public void sendData(@PathVariable Long teacherId) {
        notificationService.notify(teacherId, "data");
    }
}
