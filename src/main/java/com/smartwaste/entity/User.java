package com.smartwaste.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    private String address;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // ROLE_CITIZEN, ROLE_ADMIN, ROLE_WORKER

    @Column(name = "assigned_area")
    private String assignedArea; // For worker role

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Default Constructor
    public User() {
    }

    // All-args Constructor
    public User(Long id, String name, String email, String phone, String address, String password, String role, String assignedArea, LocalDateTime createdAt, Boolean isVerified, String verificationCode, Boolean isBlocked) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.role = role;
        this.assignedArea = assignedArea;
        this.createdAt = createdAt;
        this.isVerified = isVerified;
        this.verificationCode = verificationCode;
        this.isBlocked = isBlocked;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAssignedArea() { return assignedArea; }
    public void setAssignedArea(String assignedArea) { this.assignedArea = assignedArea; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isVerified() { return isVerified != null ? isVerified : false; }
    public void setVerified(Boolean verified) { isVerified = verified; }

    public String getVerificationCode() { return verificationCode; }
    public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }

    public boolean isBlocked() { return isBlocked != null ? isBlocked : false; }
    public void setBlocked(Boolean blocked) { isBlocked = blocked; }

    // Static Builder
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String address;
        private String password;
        private String role;
        private String assignedArea;
        private LocalDateTime createdAt;
        private Boolean isVerified;
        private String verificationCode;
        private Boolean isBlocked;

        UserBuilder() {}

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder phone(String phone) { this.phone = phone; return this; }
        public UserBuilder address(String address) { this.address = address; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder role(String role) { this.role = role; return this; }
        public UserBuilder assignedArea(String assignedArea) { this.assignedArea = assignedArea; return this; }
        public UserBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserBuilder isVerified(Boolean isVerified) { this.isVerified = isVerified; return this; }
        public UserBuilder verificationCode(String verificationCode) { this.verificationCode = verificationCode; return this; }
        public UserBuilder isBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; return this; }

        public User build() {
            return new User(id, name, email, phone, address, password, role, assignedArea, createdAt, isVerified, verificationCode, isBlocked);
        }
    }
}
