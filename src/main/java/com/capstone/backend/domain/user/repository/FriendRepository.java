package com.capstone.backend.domain.user.repository;

import com.capstone.backend.domain.user.entity.Friend;
import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    // roomId로 room 존재 여부 확인
    Optional<Friend> findByRoomId(String roomId);

    // teacher, parent 유저로 roomId 찾기
    @Query("SELECT f.roomId FROM Friend f WHERE f.teacherUserId = :teacherUserId AND f.parentUserId = :parentUserId")
    Optional<String> findRoomId(@Param("teacherUserId") Long teacherUserId, @Param("parentUserId") Long parentUserId);

    // teacherUserId와 parentUserId가 모두 일치하는 행을 찾습니다.
    Optional<Friend> findByTeacherUserIdAndParentUserId(Long teacherUserId, Long parentUserId);

    // parent_user_id로 teacher_user_id 찾기
    @Query("SELECT DISTINCT f.teacherUserId FROM Friend f WHERE f.parentUserId = :parentUserId")
    List<Long> findTeacherUserIdAsParent(@Param("parentUserId") Long parentUserId);

    List<Friend> findByParentUserId(Long parentUserId);

    List<Friend> findByTeacherUserId(Long teacherUserId);

    boolean existsByTeacherAndParent(Teacher teacher, Parent parent);
}
