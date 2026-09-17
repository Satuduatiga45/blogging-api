package com.satuduatiga.api.auth;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.satuduatiga.api.auth.dto.AuthResponse;
import com.satuduatiga.api.auth.dto.LoginRequest;
import com.satuduatiga.api.auth.dto.RefreshTokenRequest;
import com.satuduatiga.api.auth.dto.RegisterRequest;
import com.satuduatiga.api.auth.service.AuthService;
import com.satuduatiga.api.common.exception.AlreadyExistsException;
import com.satuduatiga.api.common.exception.ResourceNotFoundException;
import com.satuduatiga.api.common.exception.UnauthorizedException;
import com.satuduatiga.api.security.CustomUserDetails;
import com.satuduatiga.api.security.JwtService;
import com.satuduatiga.api.user.entity.RoleEntity;
import com.satuduatiga.api.user.entity.UserEntity;
import com.satuduatiga.api.user.repository.RoleRepository;
import com.satuduatiga.api.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    // REGISTER

    @Test
    void register_Success() {
        RegisterRequest registerRequest = new RegisterRequest("user", "user@mail.com", "password123");
        RoleEntity mockRole = new RoleEntity();
        mockRole.setName("ROLE_USER");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encryptedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(mockRole));
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("mock.access.token");
        when(jwtService.generateRefreshToken(any(CustomUserDetails.class))).thenReturn("mock.refresh.token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock.access.token", response.getAccessToken());
        assertEquals("mock.refresh.token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void register_Failed_UsernameAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest("user", "user@mail.com", "password123");
        when(userRepository.existsByUsername("user")).thenReturn(true);

        AlreadyExistsException ex = assertThrows(AlreadyExistsException.class,
                () -> authService.register(registerRequest));

        assertEquals("Username is already taken", ex.getMessage());

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void register_Failed_EmailAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest("user", "user@mail.com", "password123");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@mail.com")).thenReturn(true);

        AlreadyExistsException ex = assertThrows(AlreadyExistsException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email is already registered", ex.getMessage());

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void register_Failed_RoleNotFound() {
        RegisterRequest registerRequest = new RegisterRequest("user", "user@mail.com", "password123");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("user@mail.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> authService.register(registerRequest));

        assertEquals("Role not found", ex.getMessage());

        verify(userRepository, never()).save(any(UserEntity.class));

    }

    // LOGIN

    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest("user", "password123");
        String accessToken = "mock.access.token";
        String refreshToken = "mock.refresh.token";

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getIdentifier(), loginRequest.getPassword());
        Authentication mockAuthentication = mock(Authentication.class);
        CustomUserDetails mockUserDetails = mock(CustomUserDetails.class);

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(authenticationManager.authenticate(authenticationToken))
                .thenReturn(mockAuthentication);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(CustomUserDetails.class))).thenReturn(refreshToken);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock.access.token", response.getAccessToken());
        assertEquals("mock.refresh.token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());

        verify(authenticationManager, times(1)).authenticate(authenticationToken);
    }

    @Test
    void login_Failed_BadCredentials() {
        LoginRequest loginRequest = new LoginRequest("user", "password123");

        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(BadCredentialsException.class);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

        assertEquals("Bad Credentials", ex.getMessage());

    }

    // REFRESH TOKEN

    @Test
    void refreshToken_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("mock.refresh.token");
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword("password123");

        when(jwtService.extractSubject("mock.refresh.token")).thenReturn("user");
        when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(eq("mock.refresh.token"), any(CustomUserDetails.class))).thenReturn(true);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn("new.access.token");

        AuthResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("mock.refresh.token", response.getRefreshToken());
        assertEquals("new.access.token", response.getAccessToken());
        assertEquals(user.getUsername(), response.getUser().getUsername());

        verify(jwtService).extractSubject("mock.refresh.token");
        verify(userRepository).findByUsernameOrEmail("user", "user");
        verify(jwtService).isTokenValid(eq("mock.refresh.token"), any(CustomUserDetails.class));
        verify(jwtService).generateToken(any(CustomUserDetails.class));
    }

    @Test
    void refreshToken_Failed_UserNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest("mock.refresh.token");

        when(jwtService.extractSubject("mock.refresh.token")).thenReturn("user");
        when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> authService.refreshToken(request));

        assertEquals("Username or email cannot be found", ex.getMessage());

        verify(jwtService, never()).isTokenValid(any(), any());
    }

    @Test
    void refreshToken_Failed_TokenInvalid() {
        RefreshTokenRequest request = new RefreshTokenRequest("mock.refresh.token");
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setPassword("password123");

        when(jwtService.extractSubject("mock.refresh.token")).thenReturn("user");
        when(userRepository.findByUsernameOrEmail("user", "user")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(eq("mock.refresh.token"), any(CustomUserDetails.class))).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> authService.refreshToken(request));

        assertEquals("Invalid or expired refresh token", ex.getMessage());

        verify(jwtService, never()).generateToken(any());
    }

}
