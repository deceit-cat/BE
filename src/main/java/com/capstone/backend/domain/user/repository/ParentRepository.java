package com.capstone.backend.domain.user.repository;

import com.capstone.backend.domain.user.entity.Parent;
import com.capstone.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByUserId(Long parentUserId);
    Optional<Parent> findByUserEmail(String email);
    Optional<Parent> findByUser(User user);
}