package com.nitroxen.demo.config;

import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        loadAdminUser();
    }

    private void loadAdminUser() {
        if (userRepository.findByPhoneNumber("+1234567890").isEmpty()) {
            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@agriwealth.com")
                    .phoneNumber("+1234567890")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            log.info("Default admin user created with phone: +1234567890 and password: admin123");
        } else {
            log.info("Admin user already exists");
        }
    }
}
