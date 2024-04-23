package com.capstone.backend.domain.user.controller;

import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.repository.ChildRepository;
import com.capstone.backend.domain.user.service.ChildService;
import com.capstone.backend.domain.user.service.FriendService;
import com.capstone.backend.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;
    private final ChildService childService;
    private final UserService userService;

    public FriendController(FriendService friendService, ChildService childService, UserService userService) {
        this.friendService = friendService;
        this.childService = childService;
        this.userService = userService;
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody Map<String, Long> requestBody) {
        Long teacherUserId = requestBody.get("teacherUserId");
        Long parentUserId = requestBody.get("parentUserId");

        Teacher teacher = userService.findTeacherById(teacherUserId);
        Parent parent = userService.findParentById(parentUserId);

        if (teacher != null && parent != null) {
            friendService.acceptFriendRequest(teacher, parent);
            childService.mapTeacherToChild(teacher.getUser().getId(), parent.getUser().getId());
            return ResponseEntity.ok("Friend request accepted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Teacher or parent not found.");
        }
    }
}
