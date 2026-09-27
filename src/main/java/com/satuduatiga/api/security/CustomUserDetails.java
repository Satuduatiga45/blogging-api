package com.satuduatiga.api.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.satuduatiga.api.user.entity.UserEntity;

public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return authorities.stream().map(authority -> authority.getAuthority()).collect(Collectors.toSet());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
