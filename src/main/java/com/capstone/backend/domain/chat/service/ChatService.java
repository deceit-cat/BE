package com.capstone.backend.domain.chat.service;

import com.capstone.backend.domain.chat.dto.ChatRoomDto;
import com.capstone.backend.domain.chat.repository.ChatRepository;
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

    private final ChatRepository chatRepository;
    private Map<String, ChatRoomDto> chatRoomMap;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
        this.chatRoomMap = new HashMap<>();
    }

    /* 채팅방 생성 */
    public ChatRoomDto createRoom() {
        ChatRoomDto chatRoom = new ChatRoomDto();
        chatRoom.setRoomId(UUID.randomUUID().toString());
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom); // Hashmap 에 새로운 채팅방 저장
        return chatRoom;
    }

//    public String createChatRoom() {
//        ChatRoomDto chatRoom = new ChatRoomDto().create();
//        String roomId = chatRoom.getRoomId();
//
//        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장장
//        chatRoomMap.put(roomId, chatRoom);
//
//        return roomId;
//    }

    // 채팅방 인원 +1
    public void plusUserCnt(String roomId){
        ChatRoomDto room = chatRoomMap.get(roomId);
        room.setUserCount(room.getUserCount()+1);
        chatRoomMap.put(roomId, room);
    }

    // 채팅방 인원 -1
    public void minusUserCnt(String roomId){
        ChatRoomDto room = chatRoomMap.get(roomId);
        room.setUserCount(room.getUserCount()-1);
        chatRoomMap.put(roomId, room);
    }

    // roomID 기준으로 채팅방 찾기
    public ChatRoomDto findRoomById(String roomId) {
        return chatRoomMap.get(roomId);
    }

    /* 채팅방 삭제 */
    public void deleteRoom(String roomId) {
        chatRoomMap.remove(roomId);
    }

    // 채팅방 유저 리스트 삭제
//    public void delUser(String roomId, String userUUID){
//        ChatRoom room = chatRoomMap.get(roomId);
//        room.getUsers().remove(userUUID);
//    }

    /* 특정 유저 조회 */
//    public String getUserName(String roomId, String userUUID){
//        ChatRoom chatRoom = chatRoomMap.get(roomId);
//        return chatRoom.getUsers().get(userUUID);
//    }

    /* 채팅방 인원수 조회 */
//    public int getUserCount(String roomId) {
//        ChatRoom chatRoom = chatRoomMap.get(roomId);
//        return (chatRoom != null) ? chatRoom.getUserCount() : 0;
//    }

    /* 채팅방 내 모든 유저 조회 */
//    public List<String> getUserList(String roomId){
//        List<String> list = new ArrayList<>();
//        ChatRoom chatRoom = chatRoomMap.get(roomId);
//
//        if (chatRoom == null) {
//            log.error("해당하는 방이 존재하지 않습니다. roomId : {}", roomId);
//            return list;
//        }
//        list.addAll(chatRoom.getUsers().values());
//        return list;
//    }

    //
//    // 채팅방 전체 userlist 조회
//    public ArrayList<String> getUserList(String roomId) {
//        ArrayList<String> list = new ArrayList<>();
//
//        ChatRoom room = chatRoomMap.get(roomId);
//
//        // hashmap 을 for 문을 돌린 후
//        // value 값만 뽑아내서 list 에 저장 후 return
//        room.getUserList().forEach((key, value) -> list.add(value));
//        return list;
//    }

    // 채팅방 유저 이름 중복 확인
//    public String isDuplicateName(String roomId, String username){
//        ChatRoom room = chatRoomMap.get(roomId);
//        String tmp = username;
//
//        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
//        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
//        while(room.getUserList().containsValue(tmp)){
//            int ranNum = (int) (Math.random()*100)+1;
//
//            tmp = username+ranNum;
//        }
//
//        return tmp;
//    }

    /* 채팅방에 유저 추가 */
//    public String addUser(String roomId, String userName){
//        ChatRoom chatRoom = chatRoomMap.get(roomId);
//        if (chatRoom != null) {
//            String userUUID = UUID.randomUUID().toString();
//            User user = new User();
//
//            List<User> users = chatRoom.getUsers();
//            users.add(userName);
//            return userUUID;
//        }
//        return null;
//    }

    /* 채팅방에서 해당 유저 삭제 */
//    public void removeUser(String roomId, String userUUID){
//        ChatRoom chatRoom = chatRoomMap.get(roomId);
//        if (chatRoom != null) {
//            chatRoom.getUserList().remove(userUUID);
//        }
//    }

    // 전체 채팅방 리스트 조회
    public List<ChatRoomDto> findAllRoom(){
        // 최근 순으로 채팅방 정렬 후 반환
        List<ChatRoomDto> chatRooms = new ArrayList<>(chatRoomMap.values());
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
