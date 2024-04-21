package com.capstone.backend.domain.chat.controller;

import com.capstone.backend.domain.chat.repository.ChatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.capstone.backend.domain.chat.dto.ChatRoom;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "채팅방", description = "생성, 조회, 입장")
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRepository chatRepository;

    @GetMapping("/roomList")
    public String goChatRoom(Model model) {
        model.addAttribute("list", chatRepository.findAllRoom());
        log.info("SHOW ALL CHATROOM LIST {}", chatRepository.findAllRoom());
        return "채팅 방 리스트";
    }

    // 채팅방 생성
    // 채팅방 생성 후 다시 / 로 return
    @PostMapping("/createRoom")
    public String createRoom(@RequestBody Map<String, Long> requestBody, RedirectAttributes rttr) {
        Long teacherUserId = requestBody.get("teacherUserId");
        Long parentUserId = requestBody.get("parentUserId");

//        ChatRoom room = chatRepository.createChatRoom();
//        log.info("CREATE Chat Room {}", room);

//        saveChatRoomToFriendTable(room.getUUID());

//        rttr.addFlashAttribute("roomName", room);
        return "redirect:/list";
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
    @GetMapping("/enterRoom")
    public String roomDetail(Model model, @RequestParam String roomId){
        log.info("roomId {}", roomId);
        model.addAttribute("room", chatRepository.findRoomById(roomId));
        return "chatroom";
    }

//    @Operation(summary = "채팅방 생성")
//    @PostMapping("/createRoom")
//    public ResponseEntity<Object> createRoom(@RequestBody Map<String, String> requestBody) {
//        String name = requestBody.get("name");
////        ChatRoom room = chatService.createRoom(name);
//        log.debug("채팅방 생성 중... ");
//
//        if (room != null) {
//            log.debug("채팅방 생성 성공! {}", room);
//            return ResponseEntity.ok().body(Map.of("roomId", room.getRoomId()));
//        } else {
//            log.error("채팅방 생성 실패 :(");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "채팅방 생성에 실패했습니다. 요청 데이터를 확인해주세요."));
//        }
//    }

//    @Operation(summary = "채팅방 조회")
//    @GetMapping("/list")
//    public List<ChatRoom> roomList() {
//        try {
//            List<ChatRoom> rooms = chatService.findAllRoom();
//            log.debug("모든 채팅방 조회 {}", rooms);
//            return rooms;
//        } catch (Exception e) {
//            log.error("채팅방 조회 중 에러 발생 : {}", e.getMessage());
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 조회에 실패했습니다.");
//        }
//    }
}