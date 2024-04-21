package com.capstone.backend.domain.user.service;

import com.capstone.backend.domain.user.entity.Friend;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import com.capstone.backend.domain.user.repository.FriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {

    private final FriendRepository friendRepository;

    @Autowired
    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    public void acceptFriendRequest(Long teacherUserId, Long parentUserId) {
        Friend friend = new Friend(
                teacherUserId,
                parentUserId,
                null
        );

        friendRepository.save(friend);
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

    // 필요한 다른 메서드들을 추가할 수 있습니다.
}