package com.capstone.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST"), PARENT("ROLE_PARENT"), TEACHER("ROLE_TEACHER");
    private final String key;
}
