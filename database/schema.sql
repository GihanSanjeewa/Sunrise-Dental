-- =============================================================================
-- Sunrise Dental Clinic Appointment and Patient Management System
-- Relational Database Schema Definition (DDL)
-- Database Engine: MySQL 8.x / MariaDB
-- Character Set: utf8mb4, Collation: utf8mb4_unicode_ci
-- =============================================================================

CREATE DATABASE IF NOT EXISTS sunrise_dental_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sunrise_dental_db;

-- -----------------------------------------------------------------------------
-- 1. Table: users
-- Manages administrative, receptionist, and clinical dentist system users.
-- Enforces BCrypt password hashing and Role-Based Access Control (RBAC).
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS dentists;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL, -- 'ADMIN', 'RECEPTIONIST', 'DENTIST'
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. Table: patients
-- Records patient demographics and medical baseline information.
-- Features unique alphanumeric sequence number (e.g., P-000001).
-- -----------------------------------------------------------------------------
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_number VARCHAR(20) NOT NULL UNIQUE, -- P-000001
    full_name VARCHAR(120) NOT NULL,
    address TEXT NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL, -- 'MALE', 'FEMALE', 'OTHER'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_number (patient_number),
    INDEX idx_patient_name (full_name),
    INDEX idx_patient_contact (contact_number)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. Table: dentists
-- Stores dental healthcare practitioners, their clinical specializations,
-- and roster availability statuses.
-- -----------------------------------------------------------------------------
CREATE TABLE dentists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dentist_number VARCHAR(20) NOT NULL UNIQUE, -- D-000001
    name VARCHAR(120) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- 'AVAILABLE', 'ON_LEAVE', 'INACTIVE'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dentist_number (dentist_number),
    INDEX idx_dentist_status (availability_status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. Table: treatments
-- Treatment catalog defining procedures, descriptions, baseline treatment costs,
-- and consultation fees. (Zero hardcoding rule: prices pulled from DB).
-- -----------------------------------------------------------------------------
CREATE TABLE treatments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    treatment_code VARCHAR(20) NOT NULL UNIQUE, -- TRT-001
    treatment_name VARCHAR(120) NOT NULL,
    description TEXT,
    treatment_cost DECIMAL(10, 2) NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'INACTIVE'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_treatment_code (treatment_code),
    INDEX idx_treatment_status (status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. Table: appointments
-- Core scheduling entity linking Patient, Dentist, and Treatment.
-- Uses composite index on (dentist_id, appointment_date, appointment_time, status)
-- to enforce atomic double-booking prevention.
-- -----------------------------------------------------------------------------
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(30) NOT NULL UNIQUE, -- APT-2026-000001
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'BOOKED', -- 'BOOKED', 'CONFIRMED', 'COMPLETED', 'CANCELLED', 'NO_SHOW'
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_apt_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_apt_dentist FOREIGN KEY (dentist_id) 
        REFERENCES dentists(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_apt_treatment FOREIGN KEY (treatment_id) 
        REFERENCES treatments(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_apt_number (appointment_number),
    INDEX idx_apt_date (appointment_date),
    INDEX idx_apt_conflict_check (dentist_id, appointment_date, appointment_time, status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. Table: bills
-- Financial invoices generated per appointment. Derives charges from DB treatment
-- catalog. Applies configurable discounts and tax percentages.
-- -----------------------------------------------------------------------------
CREATE TABLE bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_number VARCHAR(30) NOT NULL UNIQUE, -- BILL-2026-000001
    appointment_id BIGINT NOT NULL UNIQUE,
    consultation_fee DECIMAL(10, 2) NOT NULL,
    treatment_cost DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    tax_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'PAID', 'PARTIALLY_PAID', 'CANCELLED'
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) 
        REFERENCES appointments(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_bill_number (bill_number),
    INDEX idx_bill_status (payment_status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 7. Table: payments
-- Settles generated bills through diverse payment channels (Strategy pattern).
-- -----------------------------------------------------------------------------
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_number VARCHAR(30) NOT NULL UNIQUE, -- PAY-2026-000001
    bill_id BIGINT NOT NULL,
    amount_paid DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL, -- 'CASH', 'CARD', 'BANK_TRANSFER'
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes VARCHAR(255),
    CONSTRAINT fk_payment_bill FOREIGN KEY (bill_id) 
        REFERENCES bills(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_payment_number (payment_number),
    INDEX idx_payment_bill (bill_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 8. Table: audit_logs
-- Immutable event ledger recording user operations for system accountability.
-- -----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id VARCHAR(50),
    details TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user (username),
    INDEX idx_audit_time (timestamp)
) ENGINE=InnoDB;
