package com.smartwaste.service;

import com.smartwaste.dto.ProfileDto;
import com.smartwaste.dto.RegisterDto;
import com.smartwaste.entity.User;
import com.smartwaste.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User registerCitizen(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account already exists with this email address");
        }
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        User user = User.builder()
                .name(registerDto.getName())
                .email(registerDto.getEmail())
                .phone(registerDto.getPhone())
                .address(registerDto.getAddress())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(registerDto.getRole() != null ? registerDto.getRole() : "ROLE_CITIZEN")
                .isVerified(true)
                .verificationCode(otp)
                .isBlocked(false)
                .build();

        User savedUser = userRepository.save(user);

        return savedUser;
    }

    @Override
    public User registerWorker(RegisterDto registerDto) {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("An account already exists with this email address");
        }
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = User.builder()
                .name(registerDto.getName())
                .email(registerDto.getEmail())
                .phone(registerDto.getPhone())
                .address(registerDto.getAddress())
                .assignedArea(registerDto.getAddress()) // Assign the provided address as territory area
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role("ROLE_WORKER")
                .isVerified(true)
                .isBlocked(false)
                .build();

        return userRepository.save(user);
    }

    @Override
    public void toggleBlockStatus(Long userId) {
        User user = findById(userId);
        if (user.getRole().equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Cannot block an admin user");
        }
        user.setBlocked(!user.isBlocked());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findById(userId);
        if (user.getRole().equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Cannot delete an admin user");
        }
        userRepository.delete(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Override
    public User updateProfile(String email, ProfileDto profileDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        user.setName(profileDto.getName());
        user.setPhone(profileDto.getPhone());
        user.setAddress(profileDto.getAddress());

        // Check if user is requesting a password change
        if (profileDto.getNewPassword() != null && !profileDto.getNewPassword().trim().isEmpty()) {
            if (profileDto.getCurrentPassword() == null || profileDto.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("Current password is required to change password");
            }
            if (!passwordEncoder.matches(profileDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Incorrect current password");
            }
            if (!profileDto.getNewPassword().equals(profileDto.getConfirmPassword())) {
                throw new IllegalArgumentException("New passwords do not match");
            }
            user.setPassword(passwordEncoder.encode(profileDto.getNewPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllWorkers() {
        return userRepository.findByRole("ROLE_WORKER");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        // Encode password if it is not already encoded
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }

        User user = userOpt.get();
        
        boolean enabled = true;
        if ("ROLE_WORKER".equals(user.getRole())) {
            enabled = user.isVerified();
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                enabled, // email verification required for workers
                true, true, !user.isBlocked(), // accountNonLocked based on isBlocked
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
