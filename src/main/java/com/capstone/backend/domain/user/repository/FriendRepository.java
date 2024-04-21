package com.capstone.backend.domain.user.repository;

import com.capstone.backend.domain.user.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f.roomId FROM Friend f WHERE f.teacherUserId = :teacherUserId OR f.parentUserId = :parentUserId")
    Long findRoomId(@Param("teacherUserId") Long teacherUserId, @Param("parentUserId") Long parentUserId);
}
