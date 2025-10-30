-- Insurance Management System Database Setup
-- Run this script in MySQL Workbench

CREATE DATABASE IF NOT EXISTS insurance_db;
USE insurance_db;

-- Create admin user (run after application starts)
-- Password: admin123 (encoded with BCrypt)
INSERT INTO users (username, email, password, full_name, phone_number, aadhaar_number, role, enabled, aadhaar_verified, created_at) 
VALUES ('admin', 'admin@insurance.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'System Administrator', '9999999999', '999999999999', 'ADMIN', true, true, NOW())
ON DUPLICATE KEY UPDATE username = username;

-- Create sample user (run after application starts)
-- Password: user123 (encoded with BCrypt)
INSERT INTO users (username, email, password, full_name, phone_number, aadhaar_number, role, enabled, aadhaar_verified, created_at) 
VALUES ('user', 'user@insurance.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Test User', '8888888888', '888888888888', 'USER', true, true, NOW())
ON DUPLICATE KEY UPDATE username = username;

-- Sample policy types for reference
-- HEALTH, LIFE, VEHICLE, HOME

-- Sample claim types for reference  
-- MEDICAL, ACCIDENT, DAMAGE, OTHER

-- Note: The application will automatically create tables using JPA/Hibernate
-- Make sure MySQL server is running on localhost:3306
-- Update application.properties with your MySQL credentials