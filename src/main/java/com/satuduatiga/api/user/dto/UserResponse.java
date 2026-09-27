package com.satuduatiga.api.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
