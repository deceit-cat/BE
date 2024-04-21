package com.capstone.backend.domain.chat.controller;

import com.capstone.backend.domain.chat.repository.ChatRepository;
import com.capstone.backend.domain.user.dto.UserDto;
import com.capstone.backend.domain.user.entity.Friend;
import com.capstone.backend.domain.user.entity.Role;
import com.capstone.backend.domain.user.entity.User;
import com.capstone.backend.domain.user.repository.FriendRepository;
import com.capstone.backend.domain.user.repository.UserRepository;
import com.capstone.backend.domain.user.service.FriendService;
import com.capstone.backend.global.jwt.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.capstone.backend.domain.chat.service.ChatService;
import com.capstone.backend.domain.chat.dto.ChatRoom;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "채팅방", description = "생성, 조회, 입장")
public class ChatRoomController {
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendService friendService;
    private final JwtService jwtService;

    @PostMapping("/createRoom")
    public String createRoom(@RequestBody Map<String, Long> requestBody,
                             @RequestHeader("Authorization") String accessToken,
                             RedirectAttributes rttr) {

        Long teacherUserId = requestBody.get("teacherUserId");
        Long parentUserId = requestBody.get("parentUserId");

        if (jwtService.isTokenValid(accessToken)) { // AccessToken이 유효한 경우
            String roomId = chatService.createChatRoom();
            log.info("채팅방 생성 : {}", roomId);

            Optional<String> userEmail = jwtService.extractEmail(accessToken);
            if (userEmail.isPresent()) {
                Optional<User> userOptional = userRepository.findByEmail(userEmail.get());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    if (user.getId().equals(teacherUserId) || user.getId().equals(parentUserId)) {
                        // 사용자 ID가 teacherUserId 또는 parentUserId와 일치하는 경우
                        // 채팅방 정보를 저장하고 리다이렉트
                        friendService.saveUUID(roomId, teacherUserId, parentUserId);
                        rttr.addFlashAttribute("roomId", roomId);
                        return "redirect:/list";
                    }
                }
            }
            // 유효한 AccessToken이지만 사용자 ID가 일치하지 않는 경우
            return "redirect:/error";
        } else {
            // AccessToken이 유효하지 않은 경우에는 처리할 작업을 추가합니다.
            // 예를 들어, 로그인 페이지로 리다이렉트하거나 에러 메시지를 반환할 수 있습니다.
            return "redirect:/login";
        }
    }

    @GetMapping("/findRoomId")
    public ResponseEntity<?> findRoomIdByTeacherIdOrParentId(@RequestBody Map<String, Long> requestBody) {
        Long teacherId = requestBody.get("teacherId");
        Long parentId = requestBody.get("parentId");

        // teacherID 또는 parentID로 해당하는 roomID 찾기
        Long roomId = friendRepository.findRoomId(teacherId, parentId);

        if (roomId != null) {
            // roomID를 body로 반환
            return ResponseEntity.ok(roomId);
        } else {
            // 해당하는 roomID가 없는 경우 예외 처리
//            return ResponseEntity.notFound().build();
            return ResponseEntity.ok("roomID가 null입니다.");
        }
    }

    @Operation(summary = "전체 채팅방 조회")
    @GetMapping("/showAllChatRooms")
    public List<ChatRoom> showAllChatRooms() {
        try {
            List<ChatRoom> chatRooms = chatService.findAllRoom();
            log.info("SHOW ALL CHATROOM LIST : {}", chatService.findAllRoom());
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
    @GetMapping("/enterRoom")
    public String roomDetail(Model model, @RequestParam String roomId) {
        log.info("roomId {}", roomId);
        model.addAttribute("room", chatService.findRoomById(roomId));
        return "chatroom";
    }
}