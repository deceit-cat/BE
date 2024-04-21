package com.capstone.backend.domain.user.repository;

import com.capstone.backend.domain.user.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, Long> {
    List<Child> findByParentUserId(Long parentUserId);
}
