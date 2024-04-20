package com.capstone.backend.domain.chat.repository;

import com.capstone.backend.domain.chat.dto.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
@Slf4j
public class ChatRepository {
    private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

    // 전체 채팅방 조회
    public List<ChatRoom> findAllRoom() {
        // 채팅방 생성 순서를 최근순으로 반환
        List chatRooms = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRooms);

        return chatRooms;
    }

    // roomID 기준으로 채팅방 찾기
    public ChatRoom findRoomById(String roomId) {
        return chatRoomMap.get(roomId);
    }

    // roomName 로 채팅방 만들기
    public ChatRoom createChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom().create(roomName); // 채팅룸 이름으로 채팅 룸 생성 후

        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장장
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);

        return chatRoom;
    }

    // 채팅방 인원 +1
    public void plusUserCnt(String roomId){
        ChatRoom room = chatRoomMap.get(roomId);
        room.setUserCount(room.getUserCount()+1);
        chatRoomMap.put(roomId, room);
    }

    // 채팅방 인원 -1
    public void minusUserCnt(String roomId){
        ChatRoom room = chatRoomMap.get(roomId);
        room.setUserCount(room.getUserCount()-1);
        chatRoomMap.put(roomId, room);
    }

    // 채팅방 유저 리스트에 유저 추가
    public String addUser(String roomId, String userName){
        ChatRoom room = chatRoomMap.get(roomId);
        String userUUID = UUID.randomUUID().toString();

        // 아이디 중복 확인 후 userList 에 추가
        room.getUserList().put(userUUID, userName);

        return userUUID;
    }

    // 채팅방 유저 이름 중복 확인
    public String isDuplicateName(String roomId, String username){
        ChatRoom room = chatRoomMap.get(roomId);
        String tmp = username;

        // 만약 userName 이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기!
        while(room.getUserList().containsValue(tmp)){
            int ranNum = (int) (Math.random()*100)+1;

            tmp = username+ranNum;
        }

        return tmp;
    }

    // 채팅방 유저 리스트 삭제
    public void delUser(String roomId, String userUUID){
        ChatRoom room = chatRoomMap.get(roomId);
        room.getUserList().remove(userUUID);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId, String userUUID){
        ChatRoom room = chatRoomMap.get(roomId);
        return room.getUserList().get(userUUID);
    }

    // 채팅방 전체 userlist 조회
    public ArrayList<String> getUserList(String roomId) {
        ArrayList<String> list = new ArrayList<>();

        ChatRoom room = chatRoomMap.get(roomId);

        // hashmap 을 for 문을 돌린 후
        // value 값만 뽑아내서 list 에 저장 후 return
        room.getUserList().forEach((key, value) -> list.add(value));
        return list;
    }

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