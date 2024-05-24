package com.capstone.backend.domain.chat.service;

import com.capstone.backend.domain.chat.entity.ChatRoom;
import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Role;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.entity.User;
import com.capstone.backend.domain.user.repository.ParentRepository;
import com.capstone.backend.domain.user.repository.TeacherRepository;
import com.capstone.backend.domain.user.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final FriendService friendService;

    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           ParentRepository parentRepository,
                           TeacherRepository teacherRepository,
                           FriendService friendService) {
        this.chatRoomRepository = chatRoomRepository;
        this.parentRepository = parentRepository;
        this.teacherRepository = teacherRepository;
        this.friendService = friendService;
    }

    /**
     * 채팅방 생성 createRoom()
     * @return chatRoom
     */
    public ChatRoom createRoom(Teacher teacher, Parent parent) {
        ChatRoom chatRoom = new ChatRoom(teacher, parent);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    /**
     *  채팅방 참여중인 인원수 조회
     */
    // 채팅방 내 인원 조회 시 사용 => 수정 필요
    public int getUserCount(String roomId) {
        Optional<ChatRoom> chatRoomOptional = Optional.ofNullable(chatRoomRepository.findByRoomId(roomId));
        return chatRoomOptional.map(chatRoom -> {
            int count = 0;
            if (chatRoom.getTeacher() != null) {
                count++;
            }
            if (chatRoom.getParent() != null) {
                count++;
            }
            return count;
        }).orElse(0);
    }

    public boolean leaveRoom(String roomId, User user) {
        Optional<ChatRoom> chatRoomOptional = Optional.ofNullable(chatRoomRepository.findByRoomId(roomId));
        if (chatRoomOptional.isPresent()) {
            ChatRoom chatRoom = chatRoomOptional.get();
            if (isUserInChatRoom(chatRoom, user)) {
                clearChatRoom(chatRoom, user);

                if (chatRoom.getTeacherUserId() == null && chatRoom.getParentUserId() == null) {
                    chatRoomRepository.delete(chatRoom);
                    friendService.deleteRoomId(roomId);
                } else {
                    chatRoomRepository.save(chatRoom);
                }
                return true;
            }
        }
        return false;
    }

    private void clearChatRoom(ChatRoom chatRoom, User user) {
        if (isTeacher(chatRoom, user)) {
            chatRoom.setTeacher(null);
            chatRoom.setTeacherUserId(null);
        }
        if (isParent(chatRoom, user)) {
            chatRoom.setParent(null);
            chatRoom.setParentUserId(null);
        }
    }

    private boolean isUserInChatRoom(ChatRoom chatRoom, User user) {
        return (chatRoom.getTeacher() != null && chatRoom.getTeacher().getUser().getId().equals(user.getId())) ||
                (chatRoom.getParent() != null && chatRoom.getParent().getUser().getId().equals(user.getId()));
    }
    private boolean isTeacher(ChatRoom chatRoom, User user) {
        return chatRoom.getTeacher() != null && chatRoom.getTeacher().getUser().getId().equals(user.getId());
    }

    private boolean isParent(ChatRoom chatRoom, User user) {
        return chatRoom.getParent() != null && chatRoom.getParent().getUser().getId().equals(user.getId());
    }

    public void deleteEmptyRooms() {
        List<ChatRoom> emptyRooms = new ArrayList<>();
        List<ChatRoom> rooms = chatRoomRepository.findAll();

        for (ChatRoom room : rooms) {
            if (room.getTeacher() == null && room.getParent() == null) {
                emptyRooms.add(room);
            }
        }
        chatRoomRepository.deleteAll(emptyRooms);
    }

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
