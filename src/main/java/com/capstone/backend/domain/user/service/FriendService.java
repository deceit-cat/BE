package com.capstone.backend.domain.user.service;

import com.capstone.backend.domain.chat.entity.ChatRoom;
import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.user.dto.FriendDto;
import com.capstone.backend.domain.user.entity.*;
import com.capstone.backend.domain.user.repository.FriendRepository;
import com.capstone.backend.domain.user.repository.ParentRepository;
import com.capstone.backend.domain.user.repository.TeacherRepository;
import com.capstone.backend.global.jwt.service.JwtService;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendService {
    private final FriendRepository friendRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final UserService userService;
    private final JwtService jwtService;
    @Autowired
    public FriendService(FriendRepository friendRepository,
                         ChatRoomRepository chatRoomRepository,
                         TeacherRepository teacherRepository,
                         ParentRepository parentRepository,
                         UserService userService,
                         JwtService jwtService) {
        this.friendRepository = friendRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public void acceptFriendRequest(Teacher teacher, Parent parent) {
        Friend friend = new Friend(
                teacher,
                parent,
                null
        );
        friendRepository.save(friend);
    }

    public boolean areFriends(Teacher teacher, Parent parent) {
        return friendRepository.existsByTeacherAndParent(teacher, parent);
    }

//    public List<String> findRoomIds(List<Long> teacherUserIds, Long parentUserId) {
//        List<String> roomIds = new ArrayList<>();
//        for (Long teacherUserId : teacherUserIds) {
//            Optional<String> roomIdOptional = friendRepository.findRoomId(teacherUserId, parentUserId);
//            roomIdOptional.ifPresent(roomIds::add);
//        }
//        return roomIds;
//    }

    public List<Long> findTeacherUserIdsAsParent(Long parentUserId) {
        try {
            Parent parent = parentRepository.findByUserId(parentUserId)
                    .orElseThrow(() -> new RuntimeException("부모를 찾을 수 없습니다. in FriendService"));

            List<Long> childTeacherIds = getChildteacherIds(parent.getUser().getId());
            if (childTeacherIds.isEmpty()) {
                throw new RuntimeException("자식의 선생님 ID를 가져올 수 없습니다.");
            }

            List<Long> friendTeacherIds = friendRepository.findTeacherUserIdAsParent(parent.getUser().getId());
            if (friendTeacherIds == null || friendTeacherIds.isEmpty()) {
                throw new RuntimeException("친구테이블에서의 선생님 ID를 가져올 수 없습니다.");
            }

            List<Long> matchingTeacherIds = new ArrayList<>();

            // 자식이 가지고 있는 선생님의 ID와 친구 목록에서 가져온 선생님의 ID를 비교하여 일치하는 ID를 찾습니다.
            for (Long childTeacherId : childTeacherIds) {
                if (friendTeacherIds.contains(childTeacherId)) {
                    // 일치하는 선생님의 ID가 발견되면 리스트에 추가합니다.
                    matchingTeacherIds.add(childTeacherId);
                }
            }
            return matchingTeacherIds;
        } catch (RuntimeException e) {
            throw new RuntimeException("부모의 ID를 찾을 수 없습니다.", e);
        }
    }

    private List<Long> getChildteacherIds(Long parentUserId) {
        // 여기에 부모의 자식 목록에서 선생님의 ID를 가져오는 로직을 구현합니다.
        // 이 예시에서는 임의의 로직을 사용하여 자식의 선생님 ID를 가져오는 것으로 가정합니다.
        List<Long> childTeacherIds = new ArrayList<>();

        // 부모의 자식 목록을 가져온다고 가정하고, 각 자식의 선생님 ID를 추출하여 리스트에 추가합니다.
        // 예를 들어, Parent 클래스에 getChildList() 메서드가 있다고 가정합니다.
        List<Child> children = parentRepository.findById(parentUserId)
                .orElseThrow(() -> new RuntimeException("parent를 찾을 수 없습니다."))
                .getChildren();

        for (Child child : children) {
            Long teacherId = child.getTeacherUserId(); // 자식이 가지고 있는 선생님의 ID를 가져옵니다.
            childTeacherIds.add(teacherId);
        }
        return childTeacherIds;
    }

    public void saveUUID(String roomId, Long teacherUserId, Long parentUserId) {
        Optional<Friend> friendOptional = friendRepository.findByTeacherUserIdAndParentUserId(teacherUserId, parentUserId);

        friendOptional.ifPresent(friend -> {
                friend.setRoomId(roomId);
                friend.setTeacherUserId(teacherUserId);
                friend.setParentUserId(parentUserId);
                friendRepository.save(friend);
        });
    }

    public Friend saveFriend(Friend friend) {
        return friendRepository.save(friend);
    }

    public List<Friend> getAllFriends() {
        return friendRepository.findAll();
    }

    public Friend getFriendById(Long id) {
        return friendRepository.findById(id).orElse(null);
    }

//    public List<Friend> getUserFriends(String email) {
//        Optional<Parent> parentOptional = parentRepository.findByEmail(email);
//        Optional<Teacher> teacherOptional = teacherRepository.findByEmail(email);
//
//        if (parentOptional.isPresent()) {
//            Long parentId = parentOptional.get().getUser().getId();
//            // 부모와 연결된 friends 열들을 가져오는 로직
//            return findUserFriends(parentId);
//        } else if (teacherOptional.isPresent()) {
//            Long teacherId = teacherOptional.get().getUser().getId();
//            // 선생님과 연결된 friends 열들을 가져오는 로직
//            return findUserFriends(teacherId);
//        } else {
//            throw new RuntimeException("유효한 사용자가 아닙니다.");
//        }
//    }

//    public boolean roomExists(String roomId) {
//        return chatRoomRepository.existsById(roomId);
//    }

    public List<FriendDto> findUserFriends(String accessToken) throws Exception {
        try {
            User user = userService.validateAccessTokenAndGetUser(accessToken);

            Long userId;
            List<Friend> userFriends;

            Optional<Parent> parentOptional = parentRepository.findByUser(user);
            Optional<Teacher> teacherOptional = teacherRepository.findByUser(user);

            if (parentOptional.isPresent()) {
                userId = parentOptional.get().getUser().getId();
                userFriends = friendRepository.findByParentUserId(userId);
            } else if (teacherOptional.isPresent()) {
                userId = teacherOptional.get().getUser().getId();
                userFriends = friendRepository.findByTeacherUserId(userId);
            } else {
                throw new RuntimeException("유효한 토큰인지 확인해주세요.");
            }

            if (userFriends.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "매핑된 값이 없습니다.");
            }

            List<FriendDto> result = new ArrayList<>();

            for (Friend friend : userFriends) {
                FriendDto friendDto = new FriendDto();
                friendDto.setParentName(friend.getParent().getUser().getName());
                friendDto.setParentUserId(friend.getParent().getUser().getId());
                friendDto.setTeacherName(friend.getTeacher().getUser().getName());
                friendDto.setTeacherUserId(friend.getTeacher().getUser().getId());
                friendDto.setRoomId(friend.getRoomId());
                result.add(friendDto);
            }
            return result;
        } catch (Exception e) {
            throw new Exception("사용자 엑세스 토큰을 검증하는 도중 오류가 발생했습니다.", e);
        }
    }

    public boolean roomExists(String roomId) {
        return roomId != null && !roomId.isEmpty();
    }
}