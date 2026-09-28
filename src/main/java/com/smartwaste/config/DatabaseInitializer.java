package com.smartwaste.config;

import com.smartwaste.entity.User;
import com.smartwaste.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public DatabaseInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create Default Admin
        if (userService.findByEmail("admin@municipal.gov") == null) {
            User admin = User.builder()
                    .name("Municipal Admin")
                    .email("admin@municipal.gov")
                    .phone("1234567890")
                    .address("Municipal Corporation HQ")
                    .password("admin123") // Will be encrypted in saveUser
                    .role("ROLE_ADMIN")
                    .build();
            userService.saveUser(admin);
            System.out.println(">>> Created Default Admin: admin@municipal.gov / admin123");
        }

        // Create Default Worker
        if (userService.findByEmail("worker@municipal.gov") == null) {
            User worker = User.builder()
                    .name("Rajesh Kumar")
                    .email("worker@municipal.gov")
                    .phone("9876543210")
                    .address("Central Sanitation Depot")
                    .assignedArea("Green Valley Area")
                    .password("worker123")
                    .role("ROLE_WORKER")
                    .build();
            userService.saveUser(worker);
            System.out.println(">>> Created Default Worker: worker@municipal.gov / worker123");
        }

        // Create Default Citizen
        if (userService.findByEmail("citizen@gmail.com") == null) {
            User citizen = User.builder()
                    .name("Amit Sharma")
                    .email("citizen@gmail.com")
                    .phone("9988776655")
                    .address("Apt 402, Sunshine Apartments, Green Valley")
                    .password("citizen123")
                    .role("ROLE_CITIZEN")
                    .build();
            userService.saveUser(citizen);
            System.out.println(">>> Created Default Citizen: citizen@gmail.com / citizen123");
        }
    }
}
