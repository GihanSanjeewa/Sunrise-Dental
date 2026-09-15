-- =============================================================================
-- Sunrise Dental Clinic Appointment and Patient Management System
-- Baseline Master Seed Data Script
-- Note: Password hashes use BCrypt algorithm.
-- Default test credentials:
--   admin / admin123 (Role: ADMIN)
--   receptionist / recept123 (Role: RECEPTIONIST)
--   dr.perera / dentist123 (Role: DENTIST)
--   dr.silva / dentist123 (Role: DENTIST)
--   password123 hash: $2a$10$eACCYoNOHEqgkHgDEmGyeeu87gV8866W7K4W758VeqR1Y9e0Jz0k.
-- =============================================================================

USE sunrise_dental_db;

-- -----------------------------------------------------------------------------
-- Seed: users
-- BCrypt hashes generated with cost factor 10
-- -----------------------------------------------------------------------------
INSERT INTO users (username, password_hash, full_name, email, role, is_active) VALUES
('admin', '$2a$10$uBZJa/Wm5SVPozp1qkn8SebomsF2PdO92iEvhqV7QWP3kLZEL0tUK', 'System Administrator', 'admin@sunrisedental.lk', 'ADMIN', TRUE),
('receptionist', '$2a$10$H8WFxB8UzbuDy7e6hQbb3erjSCec6jxLUHvt6af7CEqi35hw/mf/e', 'Kamani Jayawardena', 'kamani@sunrisedental.lk', 'RECEPTIONIST', TRUE),
('dr.perera', '$2a$10$5fpiUuUfbuz.xnZ16.qpj.iBeBCq4owEhlKTBZUmPpV6tMmoQUBuS', 'Dr. Rohan Perera', 'rohan.perera@sunrisedental.lk', 'DENTIST', TRUE),
('dr.silva', '$2a$10$5fpiUuUfbuz.xnZ16.qpj.iBeBCq4owEhlKTBZUmPpV6tMmoQUBuS', 'Dr. Anoma Silva', 'anoma.silva@sunrisedental.lk', 'DENTIST', TRUE);

-- -----------------------------------------------------------------------------
-- Seed: patients
-- Realistic Sri Lankan patient profiles
-- -----------------------------------------------------------------------------
INSERT INTO patients (patient_number, full_name, address, contact_number, email, date_of_birth, gender) VALUES
('P-000001', 'Sunil Wickramasinghe', 'No. 45, Galle Road, Colombo 03', '0771234567', 'sunil.w@gmail.com', '1985-04-12', 'MALE'),
('P-000002', 'Nadeeka Bandara', '12/A, Kandy Road, Kelaniya', '0719876543', 'nadeeka.b@yahoo.com', '1992-08-23', 'FEMALE'),
('P-000003', 'Dinesh Cooray', '78, Duplication Road, Colombo 04', '0765551234', 'dinesh.cooray@outlook.com', '1978-11-30', 'MALE'),
('P-000004', 'Sanduni Perera', '24, Baseline Road, Dematagoda', '0723334455', 'sanduni.p@gmail.com', '2001-02-15', 'FEMALE'),
('P-000005', 'Mohamed Rizwan', '105, Maligawatta Road, Colombo 10', '0754448899', 'rizwan.m@hotmail.com', '1989-06-18', 'MALE');

-- -----------------------------------------------------------------------------
-- Seed: dentists
-- Accredited dental clinicians across diverse specialties
-- -----------------------------------------------------------------------------
INSERT INTO dentists (dentist_number, name, specialization, contact_number, email, availability_status) VALUES
('D-000001', 'Dr. Rohan Perera', 'Orthodontics & Dentofacial Orthopedics', '0772221100', 'rohan.perera@sunrisedental.lk', 'AVAILABLE'),
('D-000002', 'Dr. Anoma Silva', 'Endodontics & Conservative Dentistry', '0713332211', 'anoma.silva@sunrisedental.lk', 'AVAILABLE'),
('D-000003', 'Dr. Kasun Fernando', 'General Dentistry & Dental Prosthetics', '0764443322', 'kasun.fernando@sunrisedental.lk', 'AVAILABLE'),
('D-000004', 'Dr. Shalini Senaratne', 'Pediatric Dentistry', '0725554433', 'shalini.s@sunrisedental.lk', 'ON_LEAVE');

-- -----------------------------------------------------------------------------
-- Seed: treatments
-- Treatment catalog with database-driven pricing (Prices in LKR)
-- -----------------------------------------------------------------------------
INSERT INTO treatments (treatment_code, treatment_name, description, treatment_cost, consultation_fee, status) VALUES
('TRT-001', 'Dental Consultation', 'Comprehensive clinical oral examination, diagnostic assessment, and personalized treatment planning.', 0.00, 2000.00, 'ACTIVE'),
('TRT-002', 'Teeth Cleaning (Scaling & Polishing)', 'Full mouth ultrasonic calculus debridement, plaque removal, and fluoride polish application.', 4500.00, 1500.00, 'ACTIVE'),
('TRT-003', 'Dental Composite Filling', 'Tooth-colored aesthetic resin restoration for anterior or posterior dental caries (per tooth).', 3500.00, 1500.00, 'ACTIVE'),
('TRT-004', 'Surgical Tooth Extraction', 'Routine or surgical removal of compromised, fractured, or deeply impacted teeth under local anesthesia.', 6000.00, 2000.00, 'ACTIVE'),
('TRT-005', 'Root Canal Treatment (RCT)', 'Endodontic extirpation, mechanical root canal debridement, shaping, disinfection, and hermetic obturation.', 18000.00, 2500.00, 'ACTIVE'),
('TRT-006', 'Porcelain Dental Crown', 'Custom-fabricated ceramic crown restoration providing functional and aesthetic tooth protection.', 25000.00, 2500.00, 'ACTIVE'),
('TRT-007', 'In-Clinic Teeth Whitening', 'Professional LED laser-activated whitening procedure removing intrinsic and extrinsic dental stains.', 15000.00, 2000.00, 'ACTIVE');

-- -----------------------------------------------------------------------------
-- Seed: appointments
-- Historical and active appointment bookings
-- -----------------------------------------------------------------------------
INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status, notes) VALUES
('APT-2026-000001', 1, 1, 2, '2026-09-16', '09:00:00', 'CONFIRMED', 'Routine 6-month preventive scaling and calculus removal.'),
('APT-2026-000002', 2, 2, 5, '2026-09-16', '10:30:00', 'BOOKED', 'Severe pulpalgia in upper left first molar. Requires endodontic evaluation.'),
('APT-2026-000003', 3, 3, 3, '2026-09-16', '14:00:00', 'BOOKED', 'Composite restoration for lower premolar interproximal lesion.'),
('APT-2026-000004', 4, 1, 1, '2026-09-17', '11:00:00', 'BOOKED', 'Orthodontic alignment consultation for malocclusion.'),
('APT-2026-000005', 5, 2, 4, '2026-09-15', '09:30:00', 'COMPLETED', 'Successfully extracted lower right third molar under local anesthesia.');

-- -----------------------------------------------------------------------------
-- Seed: bills
-- Calculated invoices for completed appointments
-- -----------------------------------------------------------------------------
INSERT INTO bills (bill_number, appointment_id, consultation_fee, treatment_cost, subtotal, discount_percentage, discount_amount, tax_percentage, tax_amount, total_amount, payment_status) VALUES
('BILL-2026-000001', 5, 2000.00, 6000.00, 8000.00, 5.00, 400.00, 2.50, 190.00, 7790.00, 'PAID');

-- -----------------------------------------------------------------------------
-- Seed: payments
-- Settled transaction log
-- -----------------------------------------------------------------------------
INSERT INTO payments (payment_number, bill_id, amount_paid, payment_method, notes) VALUES
('PAY-2026-000001', 1, 7790.00, 'CASH', 'Full payment received at front reception counter.');

-- -----------------------------------------------------------------------------
-- Seed: audit_logs
-- Baseline operational logs
-- -----------------------------------------------------------------------------
INSERT INTO audit_logs (username, action, entity_name, entity_id, details) VALUES
('admin', 'INITIALIZE', 'DATABASE', 'SCHEMA', 'Master database schema and baseline catalogs seeded successfully.'),
('receptionist', 'REGISTER', 'PATIENT', 'P-000001', 'Onboarded patient Sunil Wickramasinghe.'),
('receptionist', 'BOOK', 'APPOINTMENT', 'APT-2026-000001', 'Booked scaling appointment for Sunil Wickramasinghe with Dr. Rohan Perera.');
