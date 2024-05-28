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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<Map<String, Object>> leaveRoom(String roomId, User user) {
        Map<String, Object> response = new HashMap<>();
        Optional<ChatRoom> chatRoomOptional = Optional.ofNullable(chatRoomRepository.findByRoomId(roomId));
        if (chatRoomOptional.isEmpty()) {
            response.put("error", roomId + " does not Exists.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ChatRoom chatRoom = chatRoomOptional.get();

        if (!isUserInChatRoom(chatRoom, user)) {
            response.put("error", "유저가 채팅방에 속한 사람인지 확인하세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        removeUser(chatRoom, user);

        if (chatRoom.getTeacherUserId() == null && chatRoom.getParentUserId() == null) {
            chatRoomRepository.delete(chatRoom);
            friendService.deleteRoomId(roomId);
            response.put("deleteRoom", roomId);
        } else {
            chatRoomRepository.save(chatRoom);
        }
        response.put("message", user.getName() + " 유저가 방을 떠났습니다.");
        return ResponseEntity.ok(response);
    }

    private void removeUser(ChatRoom chatRoom, User user) {
        if (isTeacher(chatRoom, user)) {
            chatRoom.setTeacher(null);
            chatRoom.setTeacherUserId(null);
            chatRoom.setTeacherName(null);
        }
        if (isParent(chatRoom, user)) {
            chatRoom.setParent(null);
            chatRoom.setParentUserId(null);
            chatRoom.setParentName(null);
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
}
