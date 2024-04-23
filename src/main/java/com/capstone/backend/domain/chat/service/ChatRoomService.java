package com.capstone.backend.domain.chat.service;

import com.capstone.backend.domain.chat.dto.ChatRoomDto;
import com.capstone.backend.domain.chat.entity.Chat;
import com.capstone.backend.domain.chat.entity.ChatRoom;
import com.capstone.backend.domain.chat.repository.ChatRepository;
import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Role;
import com.capstone.backend.domain.user.entity.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    /**
     * 채팅방 생성 createRoom()
     * @return chatRoom
     */
    public ChatRoom createRoom() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomId(UUID.randomUUID().toString());
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    /**
     * roomId 로 검색한 채팅방에 참여중인 "유저" 리스트 반환
     */
    public List<String> getUserListByRoomId(String roomId) {
        List<String> userList = new ArrayList<>();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        if (chatRoom != null) {
            Teacher teacher = chatRoom.getTeacher();
            Parent parent = chatRoom.getParent();

            if (teacher != null) {
                String teacherName = teacher.getUser().getName();
                Long teacherId = teacher.getUser().getId();
                userList.add("Teacher Name: " + teacherName + ", Teacher ID: " + teacherId);
            }
            if (parent != null) {
                String parentName = parent.getUser().getName();
                Long parentId = parent.getUser().getId();
                userList.add("Parent Name: " + parentName + ", Parent ID: " + parentId);
            }
        }
        return userList;
    }

    /**
     *  채팅방 참여중인 인원수 조회
     */
    public int getUserCount(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        return (chatRoom != null) ? chatRoom.getUserCount() : 0;
    }



    /**
     *  roomId로 특정 채팅방 조회
     *
     *  변경 >> "friends의 조건들로" roomID return
     */
//    public ChatRoomDto findRoomById(String roomId) {
//        return chatRoomRepository.findById(roomId);
//    }


    /**
     * user가 가진 roomId 조회
     * @return
     */



    /**
     * 모든 채팅방 조회
     */
    public List<ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll();
    }
//    public List<ChatRoomDto> findAllRoom() {
//        List<ChatRoomDto> chatRooms = new ArrayList<>(chatRoomMap.values());
//        Collections.reverse(chatRooms);
//        return chatRooms;
//    }


    /**
     * 채팅방 삭제 deleteRoom(roomId)
     */
//    public void deleteRoom(String roomId) {
//        chatRoomRepository.deleteById(roomId);
//    }

//    public void plusUserCnt(String roomId){
//        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(roomId);
//        chatRoomOptional.ifPresent(chatRoom -> {
//            chatRoom.setUserCount(chatRoom.getUserCount() + 1);
//            chatRoomRepository.save(chatRoom);
//        });
//    }

//    public void minusUserCnt(String roomId){
//        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(roomId);
//        chatRoomOptional.ifPresent(chatRoom -> {
//            chatRoom.setUserCount(chatRoom.getUserCount() - 1);
//            chatRoomRepository.save(chatRoom);
//        });
//    }

//    // 채팅방 유저 리스트 삭제
//    public void delUser(String roomId, String userUUID){
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        room.Friend().remove(userUUID);
//
//        /* 채팅방에 유저 추가 */
//        public String addUser(String roomId, String userName){
//            ChatRoomDto chatRoom = chatRoomMap.get(roomId);
//            if (chatRoom != null) {
//                String userUUID = UUID.randomUUID().toString();
//                User user = new User();
//
//                List<User> users = chatRoom.getUsers();
//                users.add(userName);
//                return userUUID;
//            }
//            return null;
//        }
//
//        /* 채팅방에서 해당 유저 삭제 */
//        public void removeUser(String roomId, String userUUID){
//            ChatRoomDto chatRoom = chatRoomMap.get(roomId);
//            if (chatRoom != null) {
//                chatRoom.getUserList().remove(userUUID);
//            }
//        }
//    }
//
////    /* 특정 유저 조회 */
////    public String getUserName(String roomId, String userUUID){
////        ChatRoomDto chatRoom = chatRoomMap.get(roomId);
////        return chatRoom.getUsers().get(userUUID);
////    }
//
//
//
////    /* 채팅방 내 모든 userId 조회 */
////    public List<String> getUserList(String roomId){
////        List<Long> userIdList = new ArrayList<>();
////        ChatRoomDto chatRoom = chatRoomMap.get(roomId);
////
////        if (chatRoom == null) {
////            log.error("해당하는 방이 존재하지 않습니다. roomId : {}", roomId);
////        }
////
////        // ChatRoomDto에서 teacher와 parent의 유저 이름을 가져와서 userList에 추가
////        if (chatRoom.getTeacher() != null) {
////            userIdList.add(chatRoom.getTeacher().getTeacherUserId());
////        }
////        if (chatRoom.getParent() != null) {
////            userIdList.add(chatRoom.getParent().getParentUserId());
////        }
////        return null;
////    }
//
//    // 채팅방 유저 이름 중복 확인
//    public String isDuplicateName(String roomId, String username){
//        ChatRoomDto room = chatRoomMap.get(roomId);
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
}
