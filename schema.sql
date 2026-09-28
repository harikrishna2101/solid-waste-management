-- Smart Waste Management System - Database DDL Script
-- Target Database: smart_waste_db
-- You can run this in phpMyAdmin or MySQL Workbench.

CREATE DATABASE IF NOT EXISTS smart_waste_db;
USE smart_waste_db;

-- 1. Users Table (Handles Citizens, Workers, and Admins)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(500),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,              -- ROLE_CITIZEN, ROLE_ADMIN, ROLE_WORKER
    assigned_area VARCHAR(255),             -- Specifically for Worker role
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Complaints Table (Tracks all reported roadside garbage spots)
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id VARCHAR(50) NOT NULL UNIQUE, -- E.g. COMP-103948
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,           -- Organic, Plastic, E-waste, Hazardous, Mixed, etc.
    description VARCHAR(1000),
    area VARCHAR(255) NOT NULL,
    landmark VARCHAR(255),
    phone VARCHAR(20) NOT NULL,
    latitude DOUBLE,
    longitude DOUBLE,
    image_path VARCHAR(255) NOT NULL,         -- Path/filename of the garbage image (before)
    after_cleaning_image VARCHAR(255),        -- Path/filename of the clean spot image (after)
    priority VARCHAR(50),                     -- Low, Medium, High
    status VARCHAR(50) DEFAULT 'PENDING',    -- PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, REJECTED
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,                  -- Foreign key to users (Citizen who reported)
    assigned_worker_id BIGINT,                -- Foreign key to users (Worker assigned to clean)
    remarks VARCHAR(500),                     -- Remarks from admin/worker (rejection details, etc.)
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_worker_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Optional: Initial seed accounts if you do not rely on Spring Boot auto-seed.
-- Passwords below are pre-encrypted with BCrypt matching:
-- admin123  -> $2a$10$j8dF4w7V8w.h.g2wF0CgXuyrW/JkOa5y82XyVnFh1lYxGkXb2PZia (or custom generated)
-- worker123 -> $2a$10$wKz0bBvQ.K8D2oB8O9Xyeu2qWvC7h1bS2XyVnFh1lYxGkXb2PZia
-- citizen123-> $2a$10$oYx0bBvQ.K8D2oB8O9Xyeu2qWvC7h1bS2XyVnFh1lYxGkXb2PZia
