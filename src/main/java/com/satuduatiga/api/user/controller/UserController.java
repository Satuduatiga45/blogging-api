package com.satuduatiga.api.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.satuduatiga.api.common.dto.PagedResponse;
import static com.satuduatiga.api.common.mapper.PageMapper.mapToPagedResponse;
import com.satuduatiga.api.security.CustomUserDetails;
import com.satuduatiga.api.user.dto.UserRequest;
import com.satuduatiga.api.user.dto.UserResponse;
import com.satuduatiga.api.user.entity.UserEntity;
import static com.satuduatiga.api.user.mapper.UserMapper.mapToUserResponse;
import com.satuduatiga.api.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<UserEntity> userPage = userService.getAllUsers(page, size, sortBy, sortDir);
        List<UserResponse> userDto = userPage.getContent().stream()
                .map(user -> mapToUserResponse(new CustomUserDetails(user))).collect(Collectors.toList());
        return ResponseEntity
                .ok(mapToPagedResponse(userDto, page, size, userPage.getTotalElements(), userPage.getTotalPages(),
                        userPage.isLast()));

    }

    @GetMapping("currentUser")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PatchMapping("currentUser")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(userRequest));
    }

    @DeleteMapping("currentUser")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser() {
        userService.deleteCurrentUser();
        SecurityContextHolder.clearContext();
    }

    @DeleteMapping("{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        SecurityContextHolder.clearContext();
    }
}
