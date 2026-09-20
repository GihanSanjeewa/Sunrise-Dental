-- =============================================================================
-- Sunrise Dental Clinic Appointment and Patient Management System
-- Relational Database Schema Definition (DDL) - Complete Enhanced Schema
-- Database Engine: MySQL 8.x / MariaDB
-- Character Set: utf8mb4, Collation: utf8mb4_unicode_ci
-- =============================================================================

CREATE DATABASE IF NOT EXISTS sunrise_dental_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sunrise_dental_db;

-- -----------------------------------------------------------------------------
-- Clean Drop in Reverse Dependency Order
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS patient_documents;
DROP TABLE IF EXISTS purchase_order_items;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS inventory_transactions;
DROP TABLE IF EXISTS inventory_items;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS prescription_items;
DROP TABLE IF EXISTS prescriptions;
DROP TABLE IF EXISTS medications;
DROP TABLE IF EXISTS treatment_sessions;
DROP TABLE IF EXISTS treatment_plan_items;
DROP TABLE IF EXISTS treatment_plans;
DROP TABLE IF EXISTS diagnoses;
DROP TABLE IF EXISTS tooth_history;
DROP TABLE IF EXISTS dental_teeth;
DROP TABLE IF EXISTS clinical_records;
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS treatments;
DROP TABLE IF EXISTS dentists;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS users;

-- -----------------------------------------------------------------------------
-- 1. Table: users
-- Manages administrative, receptionist, and clinical dentist system users.
-- -----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL, -- 'ADMIN', 'RECEPTIONIST', 'DENTIST'
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    lockout_until TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_username (username),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. Table: patients
-- Records patient demographics and medical baseline information.
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
-- Stores dental healthcare practitioners and clinical specializations.
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
-- Treatment catalog defining procedures, baseline costs, and consultation fees.
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
-- Core scheduling entity linking Patient, Dentist, Treatment, and Daily Token.
-- -----------------------------------------------------------------------------
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_number VARCHAR(30) NOT NULL UNIQUE, -- APT-2026-000001
    token_number VARCHAR(10) NULL, -- A001, A002
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'BOOKED', -- 'BOOKED', 'CONFIRMED', 'WAITING', 'CALLED', 'IN_TREATMENT', 'COMPLETED', 'CANCELLED', 'NO_SHOW'
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
-- Financial invoices generated per appointment.
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
-- Immutable event ledger with extended user context (IP, user agent, old/new values).
-- -----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id VARCHAR(50),
    details TEXT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user (username),
    INDEX idx_audit_time (timestamp)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 9. Table: clinical_records
-- Patient clinical encounter record linked to Patient, Dentist, and Appointment.
-- -----------------------------------------------------------------------------
CREATE TABLE clinical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_number VARCHAR(30) NOT NULL UNIQUE, -- CR-2026-000001
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    appointment_id BIGINT NULL,
    visit_date DATE NOT NULL,
    chief_complaint TEXT,
    medical_history TEXT,
    allergies TEXT,
    current_medications TEXT,
    diagnosis TEXT,
    clinical_notes TEXT,
    treatment_notes TEXT,
    follow_up_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cr_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cr_dentist FOREIGN KEY (dentist_id) 
        REFERENCES dentists(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cr_appointment FOREIGN KEY (appointment_id) 
        REFERENCES appointments(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_cr_patient (patient_id),
    INDEX idx_cr_dentist (dentist_id),
    INDEX idx_cr_visit_date (visit_date)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 10. Table: dental_teeth
-- FDI tooth chart records (Adult permanent teeth: 11-18, 21-28, 31-38, 41-48).
-- -----------------------------------------------------------------------------
CREATE TABLE dental_teeth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    tooth_number INT NOT NULL, -- 11..48
    tooth_condition VARCHAR(30) NOT NULL DEFAULT 'HEALTHY', -- HEALTHY, CARIES, FILLED, MISSING, ROOT_CANAL, CROWN, EXTRACTION_REQUIRED, IMPLANT, OTHER
    status VARCHAR(50) NOT NULL DEFAULT 'SOUND',
    notes TEXT,
    last_treatment_date DATE NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tooth_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uk_patient_tooth (patient_id, tooth_number),
    INDEX idx_tooth_condition (tooth_condition)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 11. Table: tooth_history
-- Chronological procedure log for an individual tooth.
-- -----------------------------------------------------------------------------
CREATE TABLE tooth_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dental_tooth_id BIGINT NOT NULL,
    clinical_record_id BIGINT NULL,
    treatment_name VARCHAR(120) NOT NULL,
    procedure_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_th_tooth FOREIGN KEY (dental_tooth_id) 
        REFERENCES dental_teeth(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_th_clinical_record FOREIGN KEY (clinical_record_id) 
        REFERENCES clinical_records(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_th_tooth (dental_tooth_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 12. Table: diagnoses
-- Structured clinical diagnoses connected to clinical records and teeth.
-- -----------------------------------------------------------------------------
CREATE TABLE diagnoses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clinical_record_id BIGINT NULL,
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    diagnosis_code VARCHAR(30),
    diagnosis_name VARCHAR(200) NOT NULL,
    notes TEXT,
    diagnosed_date DATE NOT NULL,
    related_teeth VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, RESOLVED, CHRONIC
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_diag_cr FOREIGN KEY (clinical_record_id) 
        REFERENCES clinical_records(id) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_diag_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_diag_dentist FOREIGN KEY (dentist_id) 
        REFERENCES dentists(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_diag_patient (patient_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 13. Table: treatment_plans
-- Multi-phase clinical treatment plans with dynamic pricing.
-- -----------------------------------------------------------------------------
CREATE TABLE treatment_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_number VARCHAR(30) NOT NULL UNIQUE, -- TP-2026-000001
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PLANNED', -- PLANNED, APPROVED, IN_PROGRESS, COMPLETED, CANCELLED
    estimated_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    actual_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tp_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_tp_dentist FOREIGN KEY (dentist_id) 
        REFERENCES dentists(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_tp_patient (patient_id),
    INDEX idx_tp_status (status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 14. Table: treatment_plan_items
-- Procedure items linked to Treatment catalog and tooth numbers.
-- -----------------------------------------------------------------------------
CREATE TABLE treatment_plan_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    treatment_plan_id BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,
    tooth_number VARCHAR(30),
    quantity INT NOT NULL DEFAULT 1,
    unit_cost DECIMAL(10, 2) NOT NULL,
    estimated_cost DECIMAL(10, 2) NOT NULL,
    actual_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_tpi_plan FOREIGN KEY (treatment_plan_id) 
        REFERENCES treatment_plans(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_tpi_treatment FOREIGN KEY (treatment_id) 
        REFERENCES treatments(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_tpi_plan (treatment_plan_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 15. Table: treatment_sessions
-- Multi-session progression tracking for treatment plans.
-- -----------------------------------------------------------------------------
CREATE TABLE treatment_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    treatment_plan_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    appointment_id BIGINT NULL,
    session_number INT NOT NULL,
    session_date DATE NOT NULL,
    completed_treatment VARCHAR(255),
    related_teeth VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ts_plan FOREIGN KEY (treatment_plan_id) 
        REFERENCES treatment_plans(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ts_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_ts_dentist FOREIGN KEY (dentist_id) 
        REFERENCES dentists(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_ts_appointment FOREIGN KEY (appointment_id) 
        REFERENCES appointments(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_ts_plan (treatment_plan_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 16. Table: medications
-- Master formulary catalog for clinical prescriptions.
-- -----------------------------------------------------------------------------
CREATE TABLE medications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    generic_name VARCHAR(150),
    dosage_form VARCHAR(50) NOT NULL, -- TABLET, CAPSULE, SYRUP, GEL, MOUTHWASH, INJECTION
    default_dosage VARCHAR(100),
    default_frequency VARCHAR(100),
    instructions TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_med_name (name)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 17. Table: prescriptions
-- Prescriptions issued by dentists for patient visits.
-- -----------------------------------------------------------------------------
CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_number VARCHAR(30) NOT NULL UNIQUE, -- RX-2026-000001
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    clinical_record_id BIGINT NULL,
    prescription_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rx_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_rx_dentist FOREIGN KEY (dentist_id) 
        REFERENCES dentists(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_rx_cr FOREIGN KEY (clinical_record_id) 
        REFERENCES clinical_records(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_rx_patient (patient_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 18. Table: prescription_items
-- Itemized medications in a prescription.
-- -----------------------------------------------------------------------------
CREATE TABLE prescription_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medication_id BIGINT NULL,
    medicine_name VARCHAR(150) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    CONSTRAINT fk_rxi_prescription FOREIGN KEY (prescription_id) 
        REFERENCES prescriptions(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rxi_medication FOREIGN KEY (medication_id) 
        REFERENCES medications(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_rxi_prescription (prescription_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 19. Table: suppliers
-- Dental product, material, and pharmaceutical suppliers.
-- -----------------------------------------------------------------------------
CREATE TABLE suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_code VARCHAR(30) NOT NULL UNIQUE, -- SUP-001
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sup_code (supplier_code)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 20. Table: inventory_items
-- Dental clinical consumables, materials, instruments, and pharmaceuticals.
-- -----------------------------------------------------------------------------
CREATE TABLE inventory_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_code VARCHAR(30) NOT NULL UNIQUE, -- ITEM-001
    name VARCHAR(150) NOT NULL,
    category VARCHAR(100) NOT NULL,
    supplier_id BIGINT NULL,
    current_quantity INT NOT NULL DEFAULT 0,
    minimum_quantity INT NOT NULL DEFAULT 10,
    unit VARCHAR(30) NOT NULL, -- PIECES, BOXES, BOTTLES, TUBES, PACKS
    unit_cost DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    expiry_date DATE NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'IN_STOCK', -- IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_supplier FOREIGN KEY (supplier_id) 
        REFERENCES suppliers(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_inv_code (item_code),
    INDEX idx_inv_status (status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 21. Table: inventory_transactions
-- Audit log of all stock movements (PURCHASE, USAGE, ADJUSTMENT, RETURN, EXPIRED).
-- -----------------------------------------------------------------------------
CREATE TABLE inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL, -- PURCHASE, USAGE, ADJUSTMENT, RETURN, EXPIRED
    quantity INT NOT NULL,
    performed_by VARCHAR(50) NOT NULL,
    reason TEXT,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_invt_item FOREIGN KEY (inventory_item_id) 
        REFERENCES inventory_items(id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_invt_item (inventory_item_id),
    INDEX idx_invt_type (transaction_type)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 22. Table: purchase_orders
-- Clinic procurement orders from authorized dental suppliers.
-- -----------------------------------------------------------------------------
CREATE TABLE purchase_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_number VARCHAR(30) NOT NULL UNIQUE, -- PO-2026-000001
    supplier_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    expected_delivery_date DATE NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ORDERED', -- ORDERED, RECEIVED, CANCELLED
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) 
        REFERENCES suppliers(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_po_number (po_number),
    INDEX idx_po_status (status)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 23. Table: purchase_order_items
-- Itemized line items within a purchase order.
-- -----------------------------------------------------------------------------
CREATE TABLE purchase_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    quantity_ordered INT NOT NULL,
    unit_cost DECIMAL(10, 2) NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_poi_order FOREIGN KEY (purchase_order_id) 
        REFERENCES purchase_orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_poi_item FOREIGN KEY (inventory_item_id) 
        REFERENCES inventory_items(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_poi_order (purchase_order_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 24. Table: patient_documents
-- Metadata for uploaded clinical records, X-rays, lab reports, and referrals.
-- -----------------------------------------------------------------------------
CREATE TABLE patient_documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    clinical_record_id BIGINT NULL,
    title VARCHAR(200) NOT NULL,
    document_type VARCHAR(50) NOT NULL, -- XRAY, PHOTOGRAPH, LAB_REPORT, REFERRAL_LETTER, TREATMENT_DOCUMENT, OTHER
    file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    file_size BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    notes TEXT,
    uploaded_by VARCHAR(50) NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pdoc_patient FOREIGN KEY (patient_id) 
        REFERENCES patients(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_pdoc_cr FOREIGN KEY (clinical_record_id) 
        REFERENCES clinical_records(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_pdoc_patient (patient_id)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 25. Table: notifications
-- In-app alert system for appointments, low stock, follow-ups, and receivables.
-- -----------------------------------------------------------------------------
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL, -- APPOINTMENT_UPCOMING, APPOINTMENT_CONFIRMED, APPOINTMENT_CANCELLED, FOLLOW_UP_DUE, LOW_STOCK, OUTSTANDING_PAYMENT, TREATMENT_PLAN_UPDATE
    target_role VARCHAR(20) NOT NULL DEFAULT 'ALL', -- ALL, ADMIN, RECEPTIONIST, DENTIST
    target_username VARCHAR(50) NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    link_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notif_role (target_role),
    INDEX idx_notif_read (is_read)
) ENGINE=InnoDB;
