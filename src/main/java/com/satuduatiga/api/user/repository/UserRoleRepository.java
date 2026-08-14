package com.satuduatiga.api.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.satuduatiga.api.user.entity.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    boolean existsByUserIdAndRoleName(Long userId, String role);
}
