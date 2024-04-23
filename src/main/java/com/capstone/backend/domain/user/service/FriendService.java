package com.capstone.backend.domain.user.service;

import com.capstone.backend.domain.chat.entity.ChatRoom;
import com.capstone.backend.domain.chat.repository.ChatRoomRepository;
import com.capstone.backend.domain.user.entity.Child;
import com.capstone.backend.domain.user.entity.Friend;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.repository.FriendRepository;
import com.capstone.backend.domain.user.repository.ParentRepository;
import com.capstone.backend.domain.user.repository.TeacherRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

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

    @Autowired
    public FriendService(FriendRepository friendRepository,
                         ChatRoomRepository chatRoomRepository,
                         TeacherRepository teacherRepository,
                         ParentRepository parentRepository) {
        this.friendRepository = friendRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
    }

    public void acceptFriendRequest(Teacher teacher, Parent parent) {
        Friend friend = new Friend(
                teacher,
                parent,
                null
        );
        friendRepository.save(friend);
    }

    public boolean roomExists(String roomId) {
        return friendRepository.findByRoomId(roomId).isPresent();
    }

    /**
     * parentId로 teacherId 찾기
     * @param parentUserId
     * @return
     */
    public List<Long> findTeacherUserIdsAsParent(Long parentUserId) {
        return friendRepository.findTeacherUserIdAsParent(parentUserId);
    }

    public String findRoomId(List<Long> teacherUserIds, Long parentUserId) {
        for (Long teacherUserId : teacherUserIds) {
            Optional<String> roomIdOptional = friendRepository.findRoomId(teacherUserId, parentUserId);
            if (roomIdOptional.isPresent()) {
                return roomIdOptional.get();
            }
        }
        return null;
    }

    public List<Long> findTeacherUserIds(Parent parent) {
        List<Long> childTeacherIds = getChildteacherIds(parent);
        List<Long> friendTeacherIds = friendRepository.findTeacherUserIdAsParent(parent.getUser().getId());
        List<Long> matchingTeacherIds = new ArrayList<>();

        // 자식이 가지고 있는 선생님의 ID와 친구 목록에서 가져온 선생님의 ID를 비교하여 일치하는 ID를 찾습니다.
        for (Long childTeacherId : childTeacherIds) {
            if (friendTeacherIds.contains(childTeacherId)) {
                // 일치하는 선생님의 ID가 발견되면 리스트에 추가합니다.
                matchingTeacherIds.add(childTeacherId);
            }
        }
        return matchingTeacherIds;
    }

    private List<Long> getChildteacherIds(Parent parent) {
        // 여기에 부모의 자식 목록에서 선생님의 ID를 가져오는 로직을 구현합니다.
        // 이 예시에서는 임의의 로직을 사용하여 자식의 선생님 ID를 가져오는 것으로 가정합니다.
        List<Long> childTeacherIds = new ArrayList<>();

        // 부모의 자식 목록을 가져온다고 가정하고, 각 자식의 선생님 ID를 추출하여 리스트에 추가합니다.
        // 예를 들어, Parent 클래스에 getChildList() 메서드가 있다고 가정합니다.
        List<Child> children = parent.getChildren();

        for (Child child : children) {
            Long teacherId = child.getTeacherUserId(); // 자식이 가지고 있는 선생님의 ID를 가져옵니다.
            childTeacherIds.add(teacherId);
        }
        return childTeacherIds;
    }

    public void saveUUID(String roomId, Long teacherUserId, Long parentUserId) {
        Optional<String> roomIdOptional = friendRepository.findRoomId(teacherUserId, parentUserId);

        roomIdOptional.ifPresent(exsitingRoomId -> {
            Optional<Friend> friendOptional = friendRepository.findByTeacherUserIdAndParentUserId(teacherUserId, parentUserId);

            friendOptional.ifPresent(friend -> {
                friend.setRoomId(roomId);
                friendRepository.save(friend);

                ChatRoom chatRoom = new ChatRoom();
                chatRoom.setRoomId(roomId);
                chatRoom.setTeacher(friend.getTeacher());
                chatRoom.setParent(friend.getParent());
                chatRoomRepository.save(chatRoom);
            });
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

}