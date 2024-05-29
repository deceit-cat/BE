package com.capstone.backend.domain.chat.controller;

import com.capstone.backend.domain.chat.dto.ChatDto;
import com.capstone.backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {
    private final SimpMessageSendingOperations template;
    private final ChatService chatService;
    private final ChatRoomController chatRoomController;

    /** MessageMapping 을 통해 WebSocket 로 들어오는 메시지 처리
     * 클라이언트에서는 /pub/chat/message 로 요청하게 되고 이것을 controller 가 받아서 처리한다.
     * 처리가 완료되면 /queue/chat/{roomId} 로 메시지가 전송된다.
     */


    /**
     * queue/chat/{chatId} 구독한 클라이언트에게 메세지 publish
     * @param chat 유저가 입력한 채팅 메시지
     */
    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatDto chat) {
        log.debug("서버에서 메세지 분석 중..."); // 서버로의 송신 OK

        // AI 필터링
        String message = chat.getMessage(); // 채팅 메세지 추출
        boolean isHidden = chatService.checkMessage(message); // 검출 값 가져오기
        chat.setHidden(isHidden ? 1 : 0); // hidden 값 설정

//        String roomId = chatRoomController.createRoom(chat.getSender(), chat.getRecipient());
//        chat.setRoomId(roomId);

        chatService.saveChat(chat);

        log.debug("\"{}\" 분석 결과 {}입니다. /queue/chat/room/{} 으로 브로드캐스팅합니다.", chat.getMessage(), chat.getHidden(), chat.getRoomId());
        template.convertAndSend("/queue/chat/room/" + chat.getRoomId(), chat); // 메세지 발행
    }
}

