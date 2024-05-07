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

    /** MessageMapping 을 통해 WebSocket 로 들어오는 메시지 처리
     * 클라이언트에서는 /pub/chat/message 로 요청하게 되고 이것을 controller 가 받아서 처리한다.
     * 처리가 완료되면 /queue/chat/{roomId} 로 메시지가 전송된다.
     */

    /**
     * 클라이언트가 채팅방에 입장
     * @param chat ENTER
     * @param headerAccessor
     */
//    @MessageMapping("/chat/enterUser")
//    public void enterUser(@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) {
//        // 채팅방 유저 +1
//        chatService.plusUserCnt(chat.getRoomId());
//
//         채팅방에 유저 추가 및 UserUUID 반환
//        String userUUID = chatService.addUser(chat.getRoomId(), chat.getSender());
//
//         세션에 유저와 채팅방의 식별자를(반환 결과) 저장
//        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
//        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
//
//        log.debug("{} 님이 채팅방에 들어왔습니다. roomId : {}", chat.getSender(), chat.getRoomId());
//
//        chat.setMessage(chat.getSender() + "님 입장!");
//        template.convertAndSend("/queue/chat./room/" + chat.getRoomId(), chat);
//        ChatDto enterMessage = ChatDto.builder().type(ChatDto.MessageType.ENTER).build();
//        template.convertAndSend("/queue/chat/" + chat.getRoomId(), enterMessage);
//    }

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

        log.debug("\"{}\" 분석 결과 {}입니다. /queue/chat/room/{} 으로 브로드캐스팅합니다.", chat.getMessage(), chat.getHidden(), chat.getRoomId());
        template.convertAndSend("/queue/chat/room/" + chat.getRoomId(), chat); // 메세지 발행
    }


    /**
     * EventListener 를 통해서 유저 퇴장 확인
     * 클라이언트가 채팅방을 나감
     * @param event
     */
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//        String userUUID = (String) event.getMessage().getHeaders().get("simpSessionId");
//        String roomId = chatService.getRoomIdByUserUUID(userUUID);
//        if (roomId != null) {
//            String username = chatService.getUserName(roomId, userUUID);
//            chatService.delUser(roomId, userUUID);
//            if (username != null) {
//                ChatDto chat = ChatDto.builder()
//                        .type(ChatDto.MessageType.LEAVE)
//                        .sender(username)
//                        .message(username + " 님 퇴장!")
//                        .build();
//                template.convertAndSend("/queue/chat/room/" + roomId, chat);
//            }
//        }
//    }
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//        log.debug("DisconnEvent {}", event);
//
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
//        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
//        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
//
//        log.debug("headAccessor {}", headerAccessor);
//
//        // 채팅방 유저 -1
//        chatService.minusUserCnt(roomId);
//
////        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
////        String username = repository.getUserName(roomId, userUUID);
////        repository.delUser(roomId, userUUID);
//
//        if (roomId != null && userUUID != null) {
//            log.debug("{} 님이 채팅방을 나갔습니다.", chatService.getUserName(roomId, userUUID));
//            // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
//            String username = chatService.getUserName(roomId, userUUID);
//            chatService.delUser(roomId, userUUID);
//
//            if (username != null) {
//                log.debug("User Disconnected : " + username);
//
//                // builder 어노테이션 활용
//                ChatDto chat = ChatDto.builder()
//                        .type(ChatDto.MessageType.LEAVE)
//                        .sender(username)
//                        .message(username + " 님 퇴장!")
//                        .build();
//
//                template.convertAndSend("/queue/chat/room/" + roomId, chat);
//            }
//        }
//    }
}

