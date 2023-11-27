package com.capstone.backend.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.capstone.backend.domain.chat.service.ChatService;
import com.capstone.backend.domain.chat.dto.ChatRoom;

import java.util.List;

@Controller
@RestController
@Slf4j
@Tag(name = "채팅방", description = "조회, 생성, 입장")
@RequestMapping("/chatroom")
public class ChatRoomController {

    @Autowired
    private ChatService chatService;

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
    // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
    @Operation(summary = "채팅방 입장", description = "room Id와 일치하는 채팅방에 클라이언트 입장시킴")
    @GetMapping("/{roomId}")
    public String enterRoom(Model model, @PathVariable String roomId){
        ChatRoom room = chatService.findByRoomId(roomId);
        if (room == null) {
            log.error("Room ID {} does not exist", roomId);
            return "redirect:/error";
        }

        List<ChatRoom> allRooms = chatService.findAllRoom();
        if (allRooms.isEmpty()) {
            log.error("No rooms available");
            return "redirect:/error";
        }

        log.info("ENTER ROOM {}", room.getRoomName());
        model.addAttribute("room", room);
        return "chatroom"; // 실제 view 파일 이름으로 수정 예정
    }

    // 채팅방 리스트 화면
    @Operation(summary = "채팅방 조회", description = "살아있는 채팅방 조회")
    @GetMapping("/list")
    public String roomList(Model model){
        model.addAttribute("list", chatService.findAllRoom());
        log.info("SHOW ALL RoomList {}", chatService.findAllRoom());
        return "roomlist"; // 실제 view 파일 이름으로 수정 예정
    }

    // 채팅방 생성
    // 채팅방 생성 후 다시 /list 로 return
    @Operation(summary = "채팅방 생성", description = "room Name을 입력받아 새로운 채팅방 생성")
    @PostMapping("/join")
    public String createRoom(@RequestParam String name, RedirectAttributes rttr) {
        ChatRoom room = chatService.createChatRoom(name);
        log.info("CREATE ROOM {}", room);
        rttr.addFlashAttribute("roomName", room);
        return "redirect:/chatroom/list";
    }
}