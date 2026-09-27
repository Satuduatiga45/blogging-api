package com.satuduatiga.api.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satuduatiga.api.common.exception.AlreadyExistsException;
import com.satuduatiga.api.common.exception.ForbiddenActionException;
import com.satuduatiga.api.common.exception.ResourceNotFoundException;
import com.satuduatiga.api.security.CustomUserDetails;
import com.satuduatiga.api.user.dto.UserRequest;
import com.satuduatiga.api.user.dto.UserResponse;
import com.satuduatiga.api.user.entity.UserEntity;
import static com.satuduatiga.api.user.mapper.UserMapper.mapToUserResponse;
import com.satuduatiga.api.user.repository.UserRepository;
import com.satuduatiga.api.user.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User is not logged in");
        }
        String currentIdentifier = authentication.getName();

        UserEntity user = userRepository.findByUsernameOrEmail(currentIdentifier, currentIdentifier)
                .orElseThrow(() -> new UsernameNotFoundException("User could not be found"));

        return user;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAll(pageable).map(user -> mapToUserResponse(new CustomUserDetails(user)));
    }

    @Transactional
    public UserResponse getCurrentUser() {
        return mapToUserResponse(new CustomUserDetails(getCurrentAuthenticatedUser()));
    }

    @Transactional
    public UserResponse updateUser(UserRequest userRequest) {
        UserEntity user = getCurrentAuthenticatedUser();

        if (userRequest.getUsername() != null && !userRequest.getUsername().isBlank()) {
            if (user.getUsername().equals(userRequest.getUsername())
                    || userRepository.existsByUsername(userRequest.getUsername())) {
                throw new AlreadyExistsException("Username is already taken");
            }
            user.setUsername(userRequest.getUsername());
        }

        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        UserEntity newUser = userRepository.save(user);

        return mapToUserResponse(new CustomUserDetails(newUser));
    }

    @Transactional
    public void deleteCurrentUser() {
        UserEntity user = getCurrentAuthenticatedUser();
        userRepository.delete(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User could not be found"));
        if (userRoleRepository.existsByUserIdAndRoleName(id, "ROLE_ADMIN")) {
            throw new ForbiddenActionException("You can not delete admin");
        }
        userRepository.delete(user);
    }

}
