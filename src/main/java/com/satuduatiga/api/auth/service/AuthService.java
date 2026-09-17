package com.satuduatiga.api.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.satuduatiga.api.auth.dto.AuthResponse;
import com.satuduatiga.api.auth.dto.LoginRequest;
import com.satuduatiga.api.auth.dto.RefreshTokenRequest;
import com.satuduatiga.api.auth.dto.RegisterRequest;
import com.satuduatiga.api.common.exception.AlreadyExistsException;
import com.satuduatiga.api.common.exception.ResourceNotFoundException;
import com.satuduatiga.api.common.exception.UnauthorizedException;
import com.satuduatiga.api.security.CustomUserDetails;
import com.satuduatiga.api.security.JwtService;
import com.satuduatiga.api.user.dto.UserResponse;
import com.satuduatiga.api.user.entity.RoleEntity;
import com.satuduatiga.api.user.entity.UserEntity;
import com.satuduatiga.api.user.repository.RoleRepository;
import com.satuduatiga.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new AlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new AlreadyExistsException("Email is already registered");
        }
        UserEntity user = new UserEntity();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        RoleEntity role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        user.addRole(role);

        userRepository.save(user);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return jwtAuthResponse(userDetails, accessToken, refreshToken);

    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getIdentifier(),
                            loginRequest.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            return jwtAuthResponse(userDetails, accessToken, refreshToken);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad Credentials");
        }

    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String subject = jwtService.extractSubject(refreshToken);

        UserEntity user = userRepository.findByUsernameOrEmail(subject, subject)
                .orElseThrow(() -> new ResourceNotFoundException("Username or email cannot be found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String accessToken = jwtService.generateToken(userDetails);

        return jwtAuthResponse(userDetails, accessToken, refreshToken);

    }

    private AuthResponse jwtAuthResponse(CustomUserDetails userDetails, String accessToken, String refreshToken) {

        UserResponse userResponse = UserResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .email(userDetails.getEmail())
                .roles(userDetails.getRoles())
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .user(userResponse)
                .build();

        return response;
    }

}
