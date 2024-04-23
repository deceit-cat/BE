package com.capstone.backend.domain.chat.controller;

import com.capstone.backend.domain.chat.entity.ChatRoom;
import com.capstone.backend.domain.chat.repository.ChatRepository;
import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.chat.service.ChatRoomService;
import com.capstone.backend.domain.user.entity.User;
import com.capstone.backend.domain.user.repository.FriendRepository;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.capstone.backend.domain.chat.service.ChatService;
import com.capstone.backend.domain.chat.dto.ChatRoomDto;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Serializable;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "채팅방", description = "생성, 조회, 입장")
public class ChatRoomController {
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendService friendService;
    private final JwtService jwtService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService,
                              ChatService chatService,
                              ChatRepository chatRepository,
                              UserRepository userRepository,
                              FriendRepository friendRepository,
                              FriendService friendService,
                              JwtService jwtService) {
        this.chatRoomService = chatRoomService;
        this.chatService = chatService;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.friendService = friendService;
        this.jwtService = jwtService;
    }
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
                        // 사용자 ID가 teacherUserId 또는 parentUserId와 일치하는 경우
                        // 채팅방 정보를 저장하고 리다이렉트
                        ChatRoom chatRoom = chatRoomService.createRoom();
                        log.info("채팅방 생성 : {}", chatRoom.getRoomId());

                        friendService.saveUUID(chatRoom.getRoomId(), teacherUserId, parentUserId);
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

    /**
     * 부모의 ID 를 이용해 선생님과 연결된 방 번호 알아내기
     * @param requestBody
     * @return
     */
//    @GetMapping("/findRoomId")
//    public ResponseEntity<?> findRoomId(@RequestBody Map<String, Long> requestBody) {
//        // 부모의 아이디 가져오기
//        Long parentUserId = requestBody.get("parentId");
//        try {
//            List<Long> teacherUserIds = friendService.findTeacherUserIdsAsParent(parentUserId);
//
//            if (teacherUserIds.isEmpty()) {
//                return ResponseEntity.ok("부모와 선생님이 연결되지 않았습니다.");
//            }
//
//            String roomId = friendService.findRoomId(teacherUserIds, parentUserId); // 🧚🏻‍ 이 코드 검토하기
//
//            if (roomId != null) {
//                return ResponseEntity.ok(roomId);
//            } else {
//                return ResponseEntity.ok("roomId 가 null 입니다.");
//            }
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    /**
     * 특정 채팅방 인원 조회
     */
    @GetMapping("/userCount/{roomId}")
    public ResponseEntity<Integer> getUserCount(@PathVariable String roomId) {
        int userCount = chatRoomService.getUserCount(roomId);
        return ResponseEntity.ok(userCount);
    }

    /**
     * 유저의 채팅방 리스트 반환
     */

    /**
     * roomId 로 채팅방에 참여 중인 유저 리스트 조회
     * @param roomId
     * @return
     */
    @Operation(summary = "roomId 로 채팅방 참여 유저 리스트 조회")
    @GetMapping("/findUsers/{roomId}")
    public ResponseEntity<?> getUserListByRoomId(@PathVariable String roomId) {
        if (!friendService.roomExists(roomId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("roomId가 틀렸습니다.");
        }
        List<String> userList = chatRoomService.getUserListByRoomId(roomId);
        return ResponseEntity.ok(userList);
    }

    @Operation(summary = "전체 채팅방 조회")
    @GetMapping("/showAllChatRooms")
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

//    @GetMapping("/userChatRooms")
//    public String getUserChatRooms(@RequestHeader("Authorization") String accessToken, Model model) {
//        // 액세스 토큰을 검증하고 유효한 사용자의 ID를 가져옵니다.
//        Optional<String> userEmail = jwtService.extractEmail(accessToken);
//
//        if (userEmail.isPresent()) {
//            Optional<User> userOptional = userRepository.findByEmail(userEmail.get());
//
//            if (userOptional.isPresent()) {
//                Long userId = userOptional.get().getId();
//
//                // 해당 사용자의 채팅방 목록을 가져옵니다.
//                List<ChatRoom> userChatRooms = chatRepository.findUserChatRooms(userId);
//
//                // 가져온 채팅방 목록을 모델에 추가하여 뷰에 전달합니다.
//                model.addAttribute("chatRooms", userChatRooms);
//
//                // 로그에 채팅방 목록 정보를 남깁니다.
//                log.info("User {}'s Chat Rooms: {}", userId, userChatRooms);
//
//                // 뷰 이름을 반환하여 해당하는 뷰를 표시합니다.
//                return "user_chat_rooms"; // 적절한 뷰 이름을 사용하세요.
//            }
//        }
        // 해당 사용자를 찾을 수 없는 경우 빈 목록을 모델에 추가하여 뷰에 전달합니다.
//        model.addAttribute("chatRooms", Collections.emptyList());
//
//        // 해당하는 뷰를 표시합니다.
//        return "user_chat_rooms"; // 적절한 뷰 이름을 사용하세요.
//    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
//    @GetMapping("/enterRoom")
//    public String roomDetail(Model model, @RequestParam String roomId) {
//        log.info("roomId {}", roomId);
//        model.addAttribute("room", chatRoomService.findRoomById(roomId));
//        return "chatroom";
//    }
}