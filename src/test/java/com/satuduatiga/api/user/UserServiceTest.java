package com.satuduatiga.api.user;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.satuduatiga.api.common.exception.AlreadyExistsException;
import com.satuduatiga.api.common.exception.ForbiddenActionException;
import com.satuduatiga.api.common.exception.ResourceNotFoundException;
import com.satuduatiga.api.security.CustomUserDetails;
import com.satuduatiga.api.user.dto.UserRequest;
import com.satuduatiga.api.user.dto.UserResponse;
import com.satuduatiga.api.user.entity.UserEntity;
import com.satuduatiga.api.user.mapper.UserMapper;
import com.satuduatiga.api.user.repository.UserRepository;
import com.satuduatiga.api.user.repository.UserRoleRepository;
import com.satuduatiga.api.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Spy
    @InjectMocks
    private UserService userService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // getCurrentAuthenticatedUser
    @Test
    void getCurrentAuthenticatedUser_Success() {
        UserEntity user = new UserEntity();
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(authentication.getName()).thenReturn("testName");
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userRepository.findByUsernameOrEmail("testName", "testName")).thenReturn(Optional.of(user));

        UserEntity authenticatedUser = userService.getCurrentAuthenticatedUser();

        assertNotNull(authenticatedUser);
        assertSame(user, authenticatedUser);
    }

    @Test
    void getCurrentAuthenticatedUser_Failed_AuthenticationNull() {
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.getCurrentAuthenticatedUser());

        assertEquals("User is not logged in", ex.getMessage());
    }

    @Test
    void getCurrentAuthenticatedUser_Failed_NotAuthenticated() {
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(authentication.isAuthenticated()).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.getCurrentAuthenticatedUser());

        assertEquals("User is not logged in", ex.getMessage());
    }

    @Test
    void getCurrentAuthenticatedUser_Failed_UserNotFound() {
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        when(authentication.getName()).thenReturn("testName");
        when(authentication.isAuthenticated()).thenReturn(true);

        when(userRepository.findByUsernameOrEmail("testName", "testName")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.getCurrentAuthenticatedUser());

        assertEquals("User could not be found", ex.getMessage());
    }

    // getAllUser
    @Test
    void getAllUser_Success_Asc() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("test1");
        user1.setEmail("test1@mail.com");
        user1.setPassword("password123");

        UserEntity user2 = new UserEntity();
        user2.setUsername("test2");
        user2.setEmail("test2@mail.com");
        user2.setPassword("password123");

        Sort sort = Sort.by("id").ascending();

        List<UserEntity> userList = List.of(user1, user2);
        Page<UserEntity> pagedUser = new PageImpl<>(userList, PageRequest.of(1, 2, sort), 2);
        Page<UserResponse> userDto = pagedUser.map(user -> UserMapper.mapToUserResponse(new CustomUserDetails(user)));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(pagedUser);

        Page<UserResponse> userTest = userService.getAllUsers(1, 2, "id", "asc");

        assertNotNull(userTest);
        assertEquals(userDto, userTest);
    }

    @Test
    void getAllUser_Success_Desc() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("test1");
        user1.setEmail("test1@mail.com");
        user1.setPassword("password123");

        UserEntity user2 = new UserEntity();
        user2.setUsername("test2");
        user2.setEmail("test2@mail.com");
        user2.setPassword("password123");

        Sort sort = Sort.by("id").descending();

        List<UserEntity> userList = List.of(user1, user2);
        Page<UserEntity> pagedUser = new PageImpl<>(userList, PageRequest.of(1, 2, sort), 2);
        Page<UserResponse> userDto = pagedUser.map(user -> UserMapper.mapToUserResponse(new CustomUserDetails(user)));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(pagedUser);

        Page<UserResponse> userTest = userService.getAllUsers(1, 2, "id", "desc");

        assertNotNull(userTest);
        assertEquals(userDto, userTest);
    }

    @Test
    void getAllUser_Success_Empty() {
        Page<UserEntity> pagedUser = Page.empty();
        Page<UserResponse> emptyResponses = Page.empty();

        when(userRepository.findAll(any(Pageable.class))).thenReturn(pagedUser);

        Page<UserResponse> userTest = userService.getAllUsers(1, 2, "id", "desc");

        assertNotNull(userTest);
        assertEquals(emptyResponses, userTest);
    }

    // getCurrentUser
    @Test
    void getCurrentUser_Success() {
        UserEntity user = new UserEntity();
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        doReturn(user).when(userService).getCurrentAuthenticatedUser();

        UserResponse expectedUserResponse = UserMapper.mapToUserResponse(new CustomUserDetails(user));

        UserResponse actualUserResponse = userService.getCurrentUser();

        assertNotNull(actualUserResponse);
        assertEquals(expectedUserResponse, actualUserResponse);
    }

    // updateUser
    @Test
    void updateUser_Success() {
        UserEntity currentUser = new UserEntity();
        currentUser.setUsername("testName");
        currentUser.setEmail("testName@mail.com");
        currentUser.setPassword("password123");

        String oldUsername = currentUser.getUsername();

        UserEntity updatedUser = new UserEntity();
        updatedUser.setUsername("newName");
        updatedUser.setEmail("testName@mail.com");
        updatedUser.setPassword("newPassword");

        doReturn(currentUser).when(userService).getCurrentAuthenticatedUser();

        UserRequest request = new UserRequest();
        request.setUsername(updatedUser.getUsername());
        request.setPassword(updatedUser.getPassword());

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

        UserResponse response = userService.updateUser(request);

        assertNotNull(response);
        assertEquals(request.getUsername(), response.getUsername());
        assertNotEquals(oldUsername, response.getUsername());
    }

    @Test
    void updateUser_Failed_UsernameAlreadyTaken() {
        UserEntity currentUser = new UserEntity();
        currentUser.setUsername("testName");
        currentUser.setEmail("testName@mail.com");
        currentUser.setPassword("password123");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setUsername("newName");
        updatedUser.setEmail("testName@mail.com");
        updatedUser.setPassword("newPassword");

        doReturn(currentUser).when(userService).getCurrentAuthenticatedUser();

        UserRequest request = new UserRequest();
        request.setUsername(updatedUser.getUsername());
        request.setPassword(updatedUser.getPassword());

        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        AlreadyExistsException ex = assertThrows(AlreadyExistsException.class, () -> userService.updateUser(request));

        assertEquals("Username is already taken", ex.getMessage());

    }

    // deleteCurrentUser
    @Test
    void deleteCurrentUser_Success() {
        UserEntity currentUser = new UserEntity();
        currentUser.setUsername("testName");
        currentUser.setEmail("testName@mail.com");
        currentUser.setPassword("password123");

        doReturn(currentUser).when(userService).getCurrentAuthenticatedUser();

        userService.deleteCurrentUser();

        verify(userRepository, times(1)).delete(currentUser);
    }

    // deleteUser
    @Test
    void deleteUser_Success() {
        UserEntity user = new UserEntity();
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userRoleRepository.existsByUserIdAndRoleName(any(Long.class), eq("ROLE_ADMIN"))).thenReturn(false);

        userService.deleteUser(any(Long.class));

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_Failed_UserNotFound() {
        UserEntity user = new UserEntity();
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(any(Long.class)));

        assertEquals("User could not be found", ex.getMessage());
    }

    @Test
    void deleteUser_Failed_DeletingAdmin() {
        UserEntity user = new UserEntity();
        user.setUsername("testName");
        user.setEmail("testName@mail.com");
        user.setPassword("password123");

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userRoleRepository.existsByUserIdAndRoleName(any(Long.class), eq("ROLE_ADMIN"))).thenReturn(true);

        ForbiddenActionException ex = assertThrows(ForbiddenActionException.class,
                () -> userService.deleteUser(any(Long.class)));

        assertEquals("You can not delete admin", ex.getMessage());
    }

}
