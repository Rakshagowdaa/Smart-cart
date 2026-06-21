package com.smartcart.userservice;

import com.smartcart.userservice.entity.User;
import com.smartcart.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(UserRepository userRepository) {
        return args -> {
            Optional<User> existingAdmin = userRepository.findByEmail("admin@smartcart.com");
            if (existingAdmin.isPresent()) {
                User admin = existingAdmin.get();
                admin.setPassword("$2a$10$/KUIAJs6qY0OkbOsGNGNG.MeFk8rjPGB2rmj3imhT/yzrbccSyV.q"); // admin123
                admin.setRole("ADMIN");
                admin.setBlocked(false);
                userRepository.save(admin);
                System.out.println("DATABASE SEED: Updated existing Admin password to 'admin123'");
            } else {
                User admin = User.builder()
                        .name("System Admin")
                        .email("admin@smartcart.com")
                        .password("$2a$10$/KUIAJs6qY0OkbOsGNGNG.MeFk8rjPGB2rmj3imhT/yzrbccSyV.q") // admin123
                        .role("ADMIN")
                        .isNewUser(false)
                        .blocked(false)
                        .build();
                userRepository.save(admin);
                System.out.println("DATABASE SEED: Seeded Admin user (admin@smartcart.com / admin123)");
            }
        };
    }
}
