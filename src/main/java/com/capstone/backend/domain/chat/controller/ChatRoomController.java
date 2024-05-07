package com.capstone.backend.domain.chat.controller;

import com.capstone.backend.domain.chat.entity.ChatRoom;
import com.capstone.backend.domain.chat.repository.ChatRepository;
import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.chat.service.ChatRoomService;
import com.capstone.backend.domain.user.entity.Parent;
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
    private final FriendRepository friendRepository;
    private final FriendService friendService;
    private final JwtService jwtService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService,
                              UserRepository userRepository,
                              FriendRepository friendRepository,
                              FriendService friendService,
                              JwtService jwtService) {
        this.chatRoomService = chatRoomService;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.friendService = friendService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "채팅방 생성")
    @PostMapping("/createRoom")
    public ResponseEntity<String> createRoom(@RequestBody Map<String, Long> requestBody,
                             @RequestHeader("Authorization") String accessToken) {

        Long teacherUserId = requestBody.get("teacherUserId");
        Long parentUserId = requestBody.get("parentUserId");

        if (jwtService.isTokenValid(accessToken)) { // AccessToken이 유효한 경우
            Optional<String> userEmail = jwtService.extractEmail(accessToken);
            if (userEmail.isPresent()) {
                Optional<User> userOptional = userRepository.findByEmail(userEmail.get());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    if (user.getId().equals(teacherUserId) || user.getId().equals(parentUserId)) {
                        Optional<String> roomIdOptional = friendRepository.findRoomId(teacherUserId, parentUserId);
                        if (roomIdOptional.isPresent()) {
                            String existingRoomId = roomIdOptional.get();
                            return ResponseEntity.ok(existingRoomId);
                        }

                        // 사용자 ID가 teacherUserId 또는 parentUserId와 일치하는 경우
                        // 채팅방 정보를 저장
                        ChatRoom chatRoom = chatRoomService.createRoom(teacherUserId, parentUserId);
                        String roomId = chatRoom.getRoomId();

                        log.info("채팅방 생성 : {}", roomId);

                        friendService.saveUUID(roomId, teacherUserId, parentUserId);
                        return ResponseEntity.ok(chatRoom.getRoomId());
                    }
                }
            }
            // 유효한 AccessToken이지만 사용자 ID가 일치하지 않는 경우
            return ResponseEntity.badRequest().body("사용자의 ID가 teacher 또는 parent 에서 발견되지 않았습니다.");
        } else {
            // AccessToken이 유효하지 않은 경우에는 처리할 작업을 추가합니다.
            // 예를 들어, 로그인 페이지로 리다이렉트하거나 에러 메시지를 반환할 수 있습니다.
            return ResponseEntity.badRequest().body("access 토큰이 유효하지 않습니다.");
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