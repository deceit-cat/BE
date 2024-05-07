package com.capstone.backend.domain.chat.controller;

import com.capstone.backend.domain.chat.entity.ChatRoom;
import com.capstone.backend.domain.chat.repository.ChatRepository;
import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.chat.service.ChatRoomService;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Role;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.entity.User;
import com.capstone.backend.domain.user.repository.FriendRepository;
import com.capstone.backend.domain.user.repository.ParentRepository;
import com.capstone.backend.domain.user.repository.TeacherRepository;
import com.capstone.backend.domain.user.repository.UserRepository;
import com.capstone.backend.domain.user.service.FriendService;
import com.capstone.backend.global.jwt.service.JwtService;
import com.google.api.client.util.store.AbstractMemoryDataStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.capstone.backend.domain.chat.service.ChatService;
import com.capstone.backend.domain.chat.dto.ChatRoomDto;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.util.*;

@Slf4j
@RestController
@Tag(name = "채팅방", description = "생성, 조회, 입장")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final FriendRepository friendRepository;
    private final FriendService friendService;
    private final JwtService jwtService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService,
                              UserRepository userRepository,
                              TeacherRepository teacherRepository,
                              ParentRepository parentRepository,
                              FriendRepository friendRepository,
                              FriendService friendService,
                              JwtService jwtService) {
        this.chatRoomService = chatRoomService;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
        this.friendRepository = friendRepository;
        this.friendService = friendService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "채팅방 생성")
    @PostMapping("/createRoom")
    public ResponseEntity<String> createRoom(@RequestHeader("Authorization") String token,
                                             @RequestBody Map<String, Long> requestBody) {

        Long teacherUserId = requestBody.get("teacherUserId");
        Long parentUserId = requestBody.get("parentUserId");

        if (jwtService.isTokenValid(token)) {
            Optional<String> email = jwtService.extractEmail(token);
            if (email.isPresent()) { // jwt 토큰 유효하면
                Optional<User> userOptional = userRepository.findByEmail(email.get());
                if (userOptional.isPresent()) { // 토큰에서 추출한 유저가 db에 존재하면
                    User user = userOptional.get();
                    // 뽑아낸 id 가 받아온 json의 id와 하나라도 일치하면
                    if (user.getId().equals(teacherUserId) || user.getId().equals(parentUserId)) {
                        Optional<String> roomIdOptional = friendRepository.findRoomId(teacherUserId, parentUserId);
                        if (roomIdOptional.isPresent()) { // 이미 채팅방이 존재하면
                            String existingRoomId = roomIdOptional.get();
                            return ResponseEntity.ok(existingRoomId); // 존재하는 roomId 값 반환
                        }

                        // 채팅방 새로 생성하는 경우
                        Optional<Teacher> teacherOptional = teacherRepository.findByUserId(teacherUserId);
                        Optional<Parent> parentOptional = parentRepository.findByUserId(parentUserId);

                        if (teacherOptional.isPresent() && parentOptional.isPresent()) {
                            Teacher teacher = teacherOptional.get();
                            Parent parent = parentOptional.get();

                            if (user.getRole().equals(Role.TEACHER) || user.getRole().equals(Role.PARENT)) {
                                ChatRoom chatRoom = chatRoomService.createRoom(teacher, parent); // ChatRoom 초기화 및 roomId 생성
                                String roomId = chatRoom.getRoomId();

                                friendService.saveUUID(roomId, teacherUserId, parentUserId); // Friends 테이블에 roomId 저장
                                return ResponseEntity.ok(roomId);
                            }
                        }
                    }
                }
            }
            // 유저가 Teacher 또는 Parent 가 아닌 경우
            return ResponseEntity.badRequest().body("유저 역할이 적절하지 않습니다.");
        } else { // 유효하지 않은 jwt 토큰이면
            return ResponseEntity.badRequest().body("액세스 토큰이 유효하지 않습니다.");
        }
    }

    @Operation(summary = "전체 채팅방 조회")
    @GetMapping("/showAllRooms")
    public List<ChatRoom> showAllChatRooms() {
        try {
            List<ChatRoom> chatRooms = chatRoomService.findAllRoom();
            log.info("SHOW ALL CHATROOM LIST : {}", chatRoomService.findAllRoom());
            return chatRooms;
        } catch (Exception e) {
            log.error("전체 채팅방 조회에서 에러 발생 : {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "전체 채팅방 조회에 실패했습니다.");
        }
    }

    /**
     * 채팅방 내 인원 조회
     */
    @Operation(summary = "채팅방 내 인원 조회")
    @GetMapping("/userCount/{roomId}")
    public ResponseEntity<Integer> getUserCount(@PathVariable String roomId) {
        int userCount = chatRoomService.getUserCount(roomId);
        return ResponseEntity.ok(userCount);
    }

//    // 채팅방 입장 화면
//    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
//    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
//    @Operation(summary = "채팅방 입장")
//    @GetMapping("/enterRoom")
//    public String roomDetail(Model model, @RequestParam String roomId) {
//        log.info("roomId {}", roomId);
//        model.addAttribute("room", chatRoomService.findRoomById(roomId));
//        return "chatroom";
//    }
}