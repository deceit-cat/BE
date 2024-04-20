package com.capstone.backend.domain.chat.service;

import com.capstone.backend.domain.chat.dto.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@Service
public class ChatService {
    private Map<String, ChatRoom> chatRoomMap;

    public ChatService() {
        this.chatRoomMap = new HashMap<>();
    }

    /* 채팅방 생성 */
    public ChatRoom createRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(UUID.randomUUID().toString());
        chatRoom.setRoomName(roomName);
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom); // Hashmap 에 새로운 채팅방 저장
        return chatRoom;
    }

    /* 채팅방 삭제 */
    public void deleteRoom(String roomId) {
        chatRoomMap.remove(roomId);
    }

    /* 특정 유저 조회 */
    public String getUserName(String roomId, String userUUID){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        return chatRoom.getUserList().get(userUUID);
    }

    /* 채팅방 인원수 조회 */
    public int getUserCount(String roomId) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        return (chatRoom != null) ? chatRoom.getUserCount() : 0;
    }

    /* 채팅방 내 모든 유저 조회 */
    public List<String> getUserList(String roomId){
        List<String> list = new ArrayList<>();

        ChatRoom chatRoom = chatRoomMap.get(roomId);

        if (chatRoom == null) {
            log.error("해당하는 방이 존재하지 않습니다. roomId : {}", roomId);
            return list;
        }

        list.addAll(chatRoom.getUserList().values());
        return list;
    }

    /* 채팅방에 유저 추가 */
    public String addUser(String roomId, String userName){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        if (chatRoom != null) {
            String userUUID = UUID.randomUUID().toString();
            chatRoom.getUserList().put(userUUID,userName); // 아이디 중복 확인 후 유저 추가
            return userUUID;
        }
        return null;
    }

    /* 채팅방에서 해당 유저 삭제 */
    public void removeUser(String roomId, String userUUID){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        if (chatRoom != null) {
            chatRoom.getUserList().remove(userUUID);
        }
    }

    // 전체 채팅방 리스트 조회
    public List<ChatRoom> findAllRoom(){
        // 최근 순으로 채팅방 정렬 후 반환
        List<ChatRoom> chatRooms = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    /* roomId 로 채팅방 찾기 */
//    public ChatRoom findByRoomId(String roomId){
//        return chatRoomMap.get(roomId);
//    }

    /* AI 문제 소지의 발언 검출 */
    public boolean checkMessage(@Payload String message) {
        String baseUrl = "http://13.124.97.155:8888/";
//        String baseUrl = "http://localhost:8888/";
        String requestUrl = baseUrl + message;

        try {
            // 비동기 방식으로 HTTP 요청을 수행
            WebClient webClient = WebClient.create();
            Mono<ResponseEntity<Map>> response = webClient.get().uri(requestUrl).retrieve().toEntity(Map.class);

            // HTTP 요청의 응답을 기다려 동기적으로 처리
            ResponseEntity<Map> responseEntity = response.block();

            // 응답에서 바디 추출
            Map<String, Object> responseBody = responseEntity.getBody();

            if (responseBody != null) {
                Integer modelResult = (Integer) responseBody.get("model_result");
                //int modelResult = (int) modelResultList.get(0); // 모델 결과 확인

//                int modelResult = (int) responseBody.get("model_result");

                // 응답이 1인지 여부 반환
                return modelResult == 1;
            } else {
                // API 응답이 비어있는 경우 처리 로직 추가
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
