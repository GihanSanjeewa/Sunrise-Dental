-- =============================================================================
-- Sunrise Dental Clinic Appointment and Patient Management System
-- Baseline Master Seed Data Script - Complete Enhanced Master Data
-- Note: Password hashes use BCrypt algorithm.
-- Default test credentials:
--   admin / admin123 (Role: ADMIN)
--   receptionist / recept123 (Role: RECEPTIONIST)
--   dr.perera / dentist123 (Role: DENTIST)
--   dr.silva / dentist123 (Role: DENTIST)
-- =============================================================================

USE sunrise_dental_db;

-- -----------------------------------------------------------------------------
-- Seed: users
-- BCrypt hashes generated with cost factor 10
-- -----------------------------------------------------------------------------
INSERT INTO users (username, password_hash, full_name, email, role, is_active, failed_login_attempts) VALUES
('admin', '$2a$10$uBZJa/Wm5SVPozp1qkn8SebomsF2PdO92iEvhqV7QWP3kLZEL0tUK', 'System Administrator', 'admin@sunrisedental.lk', 'ADMIN', TRUE, 0),
('receptionist', '$2a$10$H8WFxB8UzbuDy7e6hQbb3erjSCec6jxLUHvt6af7CEqi35hw/mf/e', 'Kamani Jayawardena', 'kamani@sunrisedental.lk', 'RECEPTIONIST', TRUE, 0),
('dr.perera', '$2a$10$5fpiUuUfbuz.xnZ16.qpj.iBeBCq4owEhlKTBZUmPpV6tMmoQUBuS', 'Dr. Rohan Perera', 'rohan.perera@sunrisedental.lk', 'DENTIST', TRUE, 0),
('dr.silva', '$2a$10$5fpiUuUfbuz.xnZ16.qpj.iBeBCq4owEhlKTBZUmPpV6tMmoQUBuS', 'Dr. Anoma Silva', 'anoma.silva@sunrisedental.lk', 'DENTIST', TRUE, 0);

-- -----------------------------------------------------------------------------
-- Seed: user_permissions
-- Granular permissions assigned per staff user
-- -----------------------------------------------------------------------------
INSERT INTO user_permissions (user_id, permission) VALUES
-- Admin: Full System Permissions
(1, 'PATIENT_READ'), (1, 'PATIENT_WRITE'),
(1, 'APPOINTMENT_READ'), (1, 'APPOINTMENT_WRITE'),
(1, 'CLINICAL_READ'), (1, 'CLINICAL_WRITE'),
(1, 'CHART_READ'), (1, 'CHART_WRITE'),
(1, 'TREATMENT_PLAN_READ'), (1, 'TREATMENT_PLAN_WRITE'),
(1, 'PRESCRIPTION_READ'), (1, 'PRESCRIPTION_WRITE'),
(1, 'BILLING_READ'), (1, 'BILLING_WRITE'),
(1, 'INVENTORY_READ'), (1, 'INVENTORY_WRITE'),
(1, 'REPORTS_VIEW'), (1, 'USER_MANAGE'),
-- Receptionist: Operations, Appointments, Patients, Billing, Reports
(2, 'PATIENT_READ'), (2, 'PATIENT_WRITE'),
(2, 'APPOINTMENT_READ'), (2, 'APPOINTMENT_WRITE'),
(2, 'BILLING_READ'), (2, 'BILLING_WRITE'),
(2, 'REPORTS_VIEW'),
-- Dr. Rohan Perera: Clinical, Dental Chart, Treatment Plans, Prescriptions, Inventory Read
(3, 'PATIENT_READ'), (3, 'APPOINTMENT_READ'),
(3, 'CLINICAL_READ'), (3, 'CLINICAL_WRITE'),
(3, 'CHART_READ'), (3, 'CHART_WRITE'),
(3, 'TREATMENT_PLAN_READ'), (3, 'TREATMENT_PLAN_WRITE'),
(3, 'PRESCRIPTION_READ'), (3, 'PRESCRIPTION_WRITE'),
(3, 'INVENTORY_READ'),
-- Dr. Anoma Silva: Clinical, Dental Chart, Treatment Plans, Prescriptions, Inventory Read
(4, 'PATIENT_READ'), (4, 'APPOINTMENT_READ'),
(4, 'CLINICAL_READ'), (4, 'CLINICAL_WRITE'),
(4, 'CHART_READ'), (4, 'CHART_WRITE'),
(4, 'TREATMENT_PLAN_READ'), (4, 'TREATMENT_PLAN_WRITE'),
(4, 'PRESCRIPTION_READ'), (4, 'PRESCRIPTION_WRITE'),
(4, 'INVENTORY_READ');

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
-- Historical and active appointment bookings with daily tokens
-- -----------------------------------------------------------------------------
INSERT INTO appointments (appointment_number, token_number, patient_id, dentist_id, treatment_id, appointment_date, appointment_time, status, notes) VALUES
('APT-2026-000001', 'A001', 1, 1, 2, '2026-09-16', '09:00:00', 'CONFIRMED', 'Routine 6-month preventive scaling and calculus removal.'),
('APT-2026-000002', 'A002', 2, 2, 5, '2026-09-16', '10:30:00', 'BOOKED', 'Severe pulpalgia in upper left first molar. Requires endodontic evaluation.'),
('APT-2026-000003', 'A003', 3, 3, 3, '2026-09-16', '14:00:00', 'BOOKED', 'Composite restoration for lower premolar interproximal lesion.'),
('APT-2026-000004', 'A004', 4, 1, 1, '2026-09-17', '11:00:00', 'BOOKED', 'Orthodontic alignment consultation for malocclusion.'),
('APT-2026-000005', 'A005', 5, 2, 4, '2026-09-15', '09:30:00', 'COMPLETED', 'Successfully extracted lower right third molar under local anesthesia.');

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
-- Seed: medications
-- Formulated dental drugs
-- -----------------------------------------------------------------------------
INSERT INTO medications (name, generic_name, dosage_form, default_dosage, default_frequency, instructions, is_active) VALUES
('Amoxicillin 500mg', 'Amoxicillin Trihydrate', 'CAPSULE', '500 mg', 'Three times daily (TID)', 'Take after meals for 5 days.', TRUE),
('Augmentin 625mg', 'Amoxicillin + Clavulanic Acid', 'TABLET', '625 mg', 'Twice daily (BD)', 'Take with food for 5-7 days.', TRUE),
('Metronidazole 400mg', 'Metronidazole', 'TABLET', '400 mg', 'Three times daily (TID)', 'Avoid alcohol completely during treatment.', TRUE),
('Paracetamol 500mg', 'Acetaminophen', 'TABLET', '1000 mg', 'Every 6 hours as needed (PRN)', 'Do not exceed 4000mg in 24 hours.', TRUE),
('Ibuprofen 400mg', 'Ibuprofen', 'TABLET', '400 mg', 'Three times daily (TID)', 'Take immediately after food.', TRUE),
('Chlorhexidine Mouthwash 0.2%', 'Chlorhexidine Gluconate', 'MOUTHWASH', '10 ml', 'Twice daily (BD)', 'Rinse mouth thoroughly for 60 seconds after brushing.', TRUE),
('Miconazole Oral Gel', 'Miconazole', 'GEL', 'Application', 'Four times daily (QID)', 'Apply topically to affected oral mucosa after meals.', TRUE);

-- -----------------------------------------------------------------------------
-- Seed: suppliers
-- Certified dental suppliers
-- -----------------------------------------------------------------------------
INSERT INTO suppliers (supplier_code, name, contact_person, phone, email, address, status) VALUES
('SUP-001', 'Lanka Dental Supplies Pvt Ltd', 'Nimal Jayasuriya', '0112345678', 'sales@lankadental.lk', 'No. 120, Union Place, Colombo 02', 'ACTIVE'),
('SUP-002', 'MediEquip Healthcare Distributors', 'Chathuri Perera', '0114567890', 'info@mediequip.lk', 'No. 55, Nawala Road, Nugegoda', 'ACTIVE'),
('SUP-003', 'Prime Dental & Pharma Solutions', 'Ravi Gunaratne', '0773334455', 'ravi@primedental.lk', 'No. 88, High Level Road, Maharagama', 'ACTIVE');

-- -----------------------------------------------------------------------------
-- Seed: inventory_items
-- Baseline dental clinical supplies and materials
-- -----------------------------------------------------------------------------
INSERT INTO inventory_items (item_code, name, category, supplier_id, current_quantity, minimum_quantity, unit, unit_cost, expiry_date, status) VALUES
('ITEM-001', 'Nitrile Examination Gloves (Medium)', 'Personal Protective Equipment', 1, 150, 30, 'BOXES', 1200.00, '2028-12-31', 'IN_STOCK'),
('ITEM-002', 'Surgical 3-Ply Face Masks', 'Personal Protective Equipment', 1, 80, 25, 'BOXES', 650.00, '2027-06-30', 'IN_STOCK'),
('ITEM-003', 'Lignocaine 2% with Adrenaline Cartridges', 'Anesthetics & Pharmaceuticals', 2, 12, 20, 'BOXES', 4800.00, '2026-11-30', 'LOW_STOCK'),
('ITEM-004', '3M Filtek Universal Composite Resin (A2)', 'Restorative Materials', 1, 8, 5, 'TUBES', 7500.00, '2027-08-31', 'IN_STOCK'),
('ITEM-005', 'Glass Ionomer Luting Cement', 'Restorative Materials', 3, 6, 4, 'PACKS', 9200.00, '2027-04-15', 'IN_STOCK'),
('ITEM-006', 'Alginate Impression Material (500g)', 'Impression Materials', 2, 4, 10, 'PACKS', 2800.00, '2027-01-31', 'LOW_STOCK'),
('ITEM-007', 'Endodontic K-Files Assorted (21mm)', 'Endodontics', 3, 25, 10, 'PACKS', 1850.00, '2029-01-01', 'IN_STOCK'),
('ITEM-008', 'Dental Needles 27G Long', 'Disposables', 2, 200, 50, 'PIECES', 45.00, '2028-05-31', 'IN_STOCK');

-- -----------------------------------------------------------------------------
-- Seed: clinical_records
-- Baseline encounter record for Patient 5
-- -----------------------------------------------------------------------------
INSERT INTO clinical_records (record_number, patient_id, dentist_id, appointment_id, visit_date, chief_complaint, medical_history, allergies, current_medications, diagnosis, clinical_notes, treatment_notes, follow_up_notes) VALUES
('CR-2026-000001', 5, 2, 5, '2026-09-15', 'Severe throbbing pain in lower right wisdom tooth area.', 'Hypertension (managed). No diabetes.', 'Penicillin allergy noted.', 'Amlodipine 5mg daily.', 'Impacted lower right third molar (Tooth 48) with localized pericoronitis.', 'Oral mucosa inflamed over disto-angular impacted tooth 48.', 'Administered local anesthesia. Performed simple surgical extraction of tooth 48. Hemostasis achieved.', 'Follow-up in 7 days for review. Prescribed Augmentin alternative and analgesics.');

-- -----------------------------------------------------------------------------
-- Seed: dental_teeth
-- Baseline tooth chart for Patient 5
-- -----------------------------------------------------------------------------
INSERT INTO dental_teeth (patient_id, tooth_number, tooth_condition, status, notes, last_treatment_date) VALUES
(5, 48, 'MISSING', 'EXTRACTED', 'Extracted due to impaction and pericoronitis.', '2026-09-15'),
(5, 16, 'CARIES', 'NEEDS_TREATMENT', 'Occlusal fissure caries observed.', '2026-09-15'),
(5, 36, 'FILLED', 'RESTORED', 'Composite restoration done in 2024.', '2024-05-10');

-- -----------------------------------------------------------------------------
-- Seed: tooth_history
-- -----------------------------------------------------------------------------
INSERT INTO tooth_history (dental_tooth_id, clinical_record_id, treatment_name, procedure_date, notes) VALUES
(1, 1, 'Surgical Tooth Extraction', '2026-09-15', 'Tooth 48 extracted successfully.');

-- -----------------------------------------------------------------------------
-- Seed: diagnoses
-- -----------------------------------------------------------------------------
INSERT INTO diagnoses (clinical_record_id, patient_id, dentist_id, diagnosis_code, diagnosis_name, notes, diagnosed_date, related_teeth, status) VALUES
(1, 5, 2, 'K01.1', 'Impacted third molar with pericoronitis', 'Severe pain and localized gingival swelling', '2026-09-15', '48', 'RESOLVED');

-- -----------------------------------------------------------------------------
-- Seed: notifications
-- Baseline system alerts
-- -----------------------------------------------------------------------------
INSERT INTO notifications (title, message, type, target_role, is_read, link_url) VALUES
('Low Stock Alert', 'Lignocaine 2% with Adrenaline Cartridges is running low (12 remaining, minimum: 20).', 'LOW_STOCK', 'ADMIN', FALSE, 'inventory.html'),
('Low Stock Alert', 'Alginate Impression Material (500g) is running low (4 remaining, minimum: 10).', 'LOW_STOCK', 'ADMIN', FALSE, 'inventory.html'),
('Appointment Notice', '5 appointments scheduled for today across active clinical rosters.', 'APPOINTMENT_UPCOMING', 'ALL', FALSE, 'appointments.html');

-- -----------------------------------------------------------------------------
-- Seed: audit_logs
-- Baseline operational logs
-- -----------------------------------------------------------------------------
INSERT INTO audit_logs (username, action, entity_name, entity_id, details, ip_address) VALUES
('admin', 'INITIALIZE', 'DATABASE', 'SCHEMA', 'Master database schema and baseline catalogs seeded successfully.', '127.0.0.1'),
('receptionist', 'REGISTER', 'PATIENT', 'P-000001', 'Onboarded patient Sunil Wickramasinghe.', '127.0.0.1'),
('receptionist', 'BOOK', 'APPOINTMENT', 'APT-2026-000001', 'Booked scaling appointment for Sunil Wickramasinghe with Dr. Rohan Perera.', '127.0.0.1');
