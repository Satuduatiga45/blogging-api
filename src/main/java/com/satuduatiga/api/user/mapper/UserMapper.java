package com.satuduatiga.api.user.mapper;

import com.satuduatiga.api.user.dto.UserResponse;

public class UserMapper {

    public static UserResponse mapToUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
