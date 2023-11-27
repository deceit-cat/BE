package com.capstone.backend.domain.chat.service;

import com.capstone.backend.domain.chat.dto.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
@Slf4j
public class ChatService {
    private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    public void init(){
        chatRoomMap = new HashMap<>();
    }

    // 전체 채팅방 조회
    public List<ChatRoom> findAllRoom(){
        // 최근 순으로 채팅방 정렬 후 반환
        List chatRooms = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRooms);

        return chatRooms;
    }

    // 채팅방 찾기 (roomId)
    public ChatRoom findByRoomId(String roomId){
        return chatRoomMap.get(roomId);
    }

    // 채팅방 생성 (roomName)
    public ChatRoom createChatRoom(String roomName){
        ChatRoom chatRoom = new ChatRoom().create(roomName);
        // Hashmap에 채팅방 아이디와 만들어진 채팅방을 저장
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // 채팅방 인원 +1
    public void increaseUser(String roomId){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.setUserCount(chatRoom.getUserCount()+1);
    }

    // 채팅방 인원 -1
    public void decreaseUser(String roomId){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.setUserCount(chatRoom.getUserCount()-1);
    }

    // 채팅방 유저 리스트에 유저 추가
    public String addUser(String roomId, String userName){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        String userUUID = UUID.randomUUID().toString();
        // 아이디 중복 확인 후 userList에 추가
        chatRoom.getUserList().put(userUUID,userName);

        return userUUID;
    }

    // 채팅방 유저 이름 중복 확인
    public String isDuplicateName(String roomId,String username){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        String temp = username;

        // 만약 username이 중복이라면 랜덤한 숫자를 붙여준다.
        // 이 때 랜덤한 숫자를 붙였을때 getUserList 안에 있는 닉네임이라면 다시 랜덤한 숫자 붙이기
        while(chatRoom.getUserList().containsValue(temp)){
            int ranNum = (int)(Math.random() * 100) + 1;
            temp = username+ranNum;
        }

        return temp;
    }

    // 채팅방 유저 리스트 삭제
    public void deleteUser(String roomId,String userUUID){
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        chatRoom.getUserList().remove(userUUID);
    }

    // 채팅방 userName 조회
    public String getUserName(String roomId,String userUUID){
        ChatRoom chatRoom = chatRoomMap.get(roomId);

        return chatRoom.getUserList().get(userUUID);
    }

    // 채팅방 전체 userList 조회
    public List<String> getUserList(String roomId){
        List<String> list = new ArrayList<>();

        ChatRoom chatRoom = chatRoomMap.get(roomId);

        if (chatRoom != null) {
            chatRoom.getUserList().forEach((key,value) -> list.add(value));
        } else {
            log.error("Room ID {} 채팅방이 존재하지 않습니다.", roomId);
        }

        return list;
    }
}
