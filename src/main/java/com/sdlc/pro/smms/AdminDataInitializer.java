package com.sdlc.pro.smms;

import com.sdlc.pro.smms.entity.AdminUser;
import com.sdlc.pro.smms.enums.Role;
import com.sdlc.pro.smms.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDataInitializer implements ApplicationRunner {

    private final AdminUserRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.admin.email}")
    private String email;

    @Value("${app.default.admin.password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!adminRepository.existsByEmail(email)) {
            AdminUser admin = new AdminUser();
            admin.setName("System Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            adminRepository.save(admin);
            log.info("App default admin user created");
        } else {
            log.info("App default admin user already exists");
        }
    }
}

