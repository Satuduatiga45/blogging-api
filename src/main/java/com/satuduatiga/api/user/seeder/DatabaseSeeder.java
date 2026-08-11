package com.satuduatiga.api.user.seeder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.satuduatiga.api.user.entity.RoleEntity;
import com.satuduatiga.api.user.entity.UserEntity;
import com.satuduatiga.api.user.repository.RoleRepository;
import com.satuduatiga.api.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // admin
    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // add ROLE_USER and ROLE_ADMIN to roles database
        RoleEntity userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder().name("ROLE_USER").build()));

        RoleEntity adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(
                        RoleEntity.builder().name("ROLE_ADMIN").build()));

        // add admin
        if (!userRepository.existsByUsername(adminUsername)) {
            UserEntity admin = new UserEntity();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.addRole(adminRole);

            userRepository.save(admin);
        }

    }

}
