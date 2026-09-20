# Sunrise Dental Clinic Appointment and Patient Management System

[![Java](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.x-blue.svg)](https://www.mysql.com/)
[![Tests](https://img.shields.io/badge/Tests-29%20Passing-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-Academic%20Project-lightgrey.svg)]()

> A complete, secure, test-driven, distributed enterprise application engineered for **Sunrise Dental Clinic** in Colombo, Sri Lanka. Built to digitize clinical records, maintain interactive FDI tooth charting, prevent double-bookings, automate database-driven billing and inventory replenishment, manage patient queues, and generate printable official receipts and prescriptions.

---

## 1. Project Overview & Business Problem

Sunrise Dental Clinic is a busy multi-practitioner dental care center in Colombo. Prior manual paper registers caused critical operational bottlenecks:
* Overlapping appointment slots (double-bookings) for attending dentists.
* Disjointed patient history, absent dental tooth charting, and misplaced clinical notes.
* Manual queue confusion in the reception lobby with no token calling mechanism.
* Arithmetic inaccuracies in billing, discount, and tax calculations.
* Lack of inventory tracking for dental consumables (anesthesia, resins, needles) and manual purchase order tracking.
* No centralized repository for patient X-rays, lab reports, and consent forms.
* High appointment no-show rates with no predictive risk assessment.

This enhanced system delivers a robust, distributed client-server architecture with REST web services, role-based access control, dynamic database-driven pricing, an interactive Bootstrap 5 medical frontend, and 29 automated test cases.

---

## 2. Key System Features

### Core Operations
* **Authentication & RBAC:** Salted BCrypt password encryption, session token authentication, account lockout after 5 consecutive failed login attempts, password strength validation, and role-based access for `ADMIN`, `RECEPTIONIST`, and `DENTIST`.
* **Patient Management:** Auto-generated sequence IDs (`P-000001`), demographic validations, contact uniqueness checks, full CRUD, and chronological medical history.
* **Dentist Management:** Roster tracking, specialization assignment, availability toggles (`AVAILABLE`, `ON_LEAVE`, `INACTIVE`), and schedule lookups.
* **Treatment Catalog (Zero Hardcoding):** Procedure catalog where consultation fees and treatment costs are pulled dynamically from the database.
* **Atomic Double-Booking Prevention:** Atomic verification queries preventing dentists from being scheduled for overlapping active slots (`SCHEDULED`/`CONFIRMED`/`WAITING`/`CALLED`/`IN_TREATMENT`). Throws structured HTTP 409 Conflict: *"Selected dentist is already booked for this time."*
* **Dynamic Billing Engine:** Calculates invoices dynamically:
  $$\text{Subtotal} = \text{Consultation Fee} + \text{Treatment Cost}$$
  $$\text{Total} = \text{Subtotal} - \text{Discount} + \text{Tax}$$
* **Payment Processing (Strategy Pattern):** Support for `CASH`, `CARD`, and `BANK_TRANSFER` methods.
* **Printable Receipts (Factory Pattern):** Browser-printable receipts formatted with clinic header, itemized charges, and clinic footer note.
* **Executive Dashboard & Analytics:** Real-time KPI summary cards and 8 multi-dimensional reports with date filters, print preview, and CSV export.
* **Audit Trail Ledger:** Immutable record of all system events, status transitions, IP addresses, and user agents.

### Enhanced Clinical & Practice Management
* **360° Patient Profile (`patient-profile.html`):** Unified clinical file showing demographics, dental chart, clinical history, diagnoses, treatment plans, prescriptions, documents, and past appointments.
* **Interactive FDI 32-Tooth Chart:** Complete adult permanent dentition (FDI 11–18, 21–28, 31–38, 41–48) arranged in 4 quadrants (Maxillary Upper Right/Left, Mandibular Lower Right/Left) with condition tracking (`HEALTHY`, `CARIES`, `FILLED`, `CROWN`, `ROOT_CANAL`, `IMPLANT`, `MISSING`, `TO_EXTRACT`, `IMPACTED`) and historical transition logging.
* **Chronological Clinical Records:** Digital examination logs capturing chief complaints, intraoral findings, periodontal status, occlusion, and diagnostic observations with unique `CR-YYYY-XXXXXX` identifiers.
* **Structured Diagnoses:** Standardized diagnosis catalog linked to patients, specific teeth, severity levels (`MILD`, `MODERATE`, `SEVERE`), and clinical records.
* **Treatment Planning & Sessions:** Multi-procedure treatment plans with dynamic catalog pricing, sequence tracking, status updates (`PLANNED`, `APPROVED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`), and individual treatment sessions.
* **Daily Queue & Token System (`queue.html`):** Daily token sequencing (`A001`, `A002`, ...), live chair statuses (`WAITING`, `CALLED`, `IN_TREATMENT`, `COMPLETED`, `NO_SHOW`), and audio-visual calling.
* **AI/ML-Ready No-Show Risk Assessment:** Real-time predictive risk scoring (0–100%, `LOW`/`MEDIUM`/`HIGH`) evaluating historical no-show rates, lead time, hour-of-day, and procedure complexity with contributing factor transparency and clinical disclaimer.
* **Dedicated Dentist Workspace (`dentist-workspace.html`):** Tailored practitioner view featuring today's schedule, live "In-Chair" patient banner, and instant shortcuts to tooth charting and examination records.
* **Medication Master Formulary & Printable Rx:** Drug catalog with dosage forms, strengths, and standard instructions, combined with official printable prescription letterheads (Rx).
* **Inventory & Supply Chain (`inventory.html`):** Dental consumable tracking, reorder thresholds, low-stock alerts, stock adjustments (`USAGE`, `WASTAGE`, `RETURN`, `ADJUSTMENT`), supplier directory, and purchase orders.
* **Automated Goods Receipt:** Marking a Purchase Order as `RECEIVED` automatically increments inventory stock quantities and records `PURCHASE` ledger transactions.
* **Patient Document Management:** Secure file storage for dental radiographs (X-Rays), lab reports, and signed consent forms under `uploads/documents/` with database metadata tracking and download endpoints.
* **In-App Notification Engine:** Real-time alerts for low consumable stock, appointment updates, and system events.

---

## 3. Technology Stack

* **Backend:** Java 17 LTS, Spring Boot 3.2.4, Spring Web (REST API), Spring Data JPA, Hibernate ORM, Spring Security, Maven 3.9.6.
* **Database:** MySQL 8.x / MariaDB (XAMPP compatible).
* **Testing:** JUnit 5, Mockito, Spring Boot Test, MockMvc, H2 in-memory test database (29/29 passing).
* **API Documentation:** Springdoc OpenAPI / Swagger UI 2.3.0 (`/swagger-ui.html`).
* **Frontend:** HTML5, CSS3 (Medical Design System with Teal `#0d9488` & Slate `#0f172a`), JavaScript (ES6 Fetch API), Bootstrap 5.3.3.
* **Security:** Spring Security, BCrypt (strength 10), Account Lockout, Secure Password Change.

---

## 4. Software Architecture & Database Schema

The project enforces a clean 4-tier layered architecture:
```
Presentation Layer (Bootstrap 5.3.3 / ES6 Vanilla JS / Medical Design System)
       ↓
REST Controller Layer (20 Spring RestControllers with Swagger OpenAPI)
       ↓
Service Layer (Business Rules, Conflict Validation, Inventory Automation)
       ↓
Repository Layer (25 Spring Data JPA / Hibernate Repositories)
       ↓
Relational Database (MySQL 8.x / 25 Normalized Tables)
```

### Relational Database Schema (25 Tables)
1. `users` - Staff accounts, BCrypt passwords, roles (`ADMIN`, `RECEPTIONIST`, `DENTIST`), failed login tracking.
2. `patients` - Patient demographics, unique `P-XXXXXX` sequence, contact info.
3. `dentists` - Dentist profiles, `D-XXXXXX` codes, specializations, availability.
4. `treatments` - Clinical procedure catalog, consultation fees, treatment costs.
5. `appointments` - Scheduled visits, double-booking validation, token numbers (`A001`), statuses (`SCHEDULED`, `CONFIRMED`, `WAITING`, `CALLED`, `IN_TREATMENT`, `COMPLETED`, `CANCELLED`, `NO_SHOW`).
6. `bills` - Itemized invoices, subtotal, discount, tax, total amount.
7. `payments` - Payment ledger, transaction IDs, payment methods (`CASH`, `CARD`, `BANK_TRANSFER`).
8. `audit_logs` - System audit trail, IP address, user agent, action logs.
9. `clinical_records` - Chronological clinical examinations (`CR-YYYY-XXXXXX`), chief complaint, findings, periodontal/occlusion notes.
10. `dental_teeth` - Patient tooth records (32 FDI teeth 11–48), condition (`HEALTHY`, `CARIES`, `CROWN`, etc.).
11. `tooth_history` - Historical record of tooth condition transitions and procedures.
12. `diagnoses` - Patient diagnoses linked to clinical records and FDI teeth.
13. `treatment_plans` - Multi-stage treatment plans (`TP-YYYY-XXXXXX`), estimated/actual costs, status.
14. `treatment_plan_items` - Procedures within a plan, tooth numbers, dynamic catalog pricing.
15. `treatment_sessions` - Execution logs for treatment plan procedures with chair notes.
16. `medications` - Formulary master catalog, brand/generic names, forms, strengths, standard instructions.
17. `prescriptions` - Official patient prescriptions (`RX-YYYY-XXXXXX`), notes, issue date.
18. `prescription_items` - Prescribed drugs, dosage, frequency, duration, instructions.
19. `suppliers` - Dental consumable vendors (`SUP-XXXX`), contact details, addresses.
20. `inventory_items` - Dental stock catalog (`ITEM-XXXX`), current quantity, reorder thresholds, unit cost.
21. `inventory_transactions` - Stock ledger (`PURCHASE`, `USAGE`, `WASTAGE`, `RETURN`, `ADJUSTMENT`).
22. `purchase_orders` - Replenishment orders (`PO-YYYY-XXXXXX`), supplier, status (`DRAFT`, `ORDERED`, `RECEIVED`, `CANCELLED`).
23. `purchase_order_items` - Line items in purchase orders, quantities, unit prices.
24. `patient_documents` - Uploaded X-rays, lab reports, consent forms, filesystem paths.
25. `notifications` - In-app notification messages, low stock alerts, target roles/users.

---

## 5. Default Test Credentials

| Role | Username | Default Password | Access Permissions |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` | Full access: All modules, Inventory, Suppliers, Users, Audits, Reports |
| **RECEPTIONIST** | `receptionist` | `recept123` | Patients, Daily Queue, Appointments, Billing, Payments, Receipts, Reports |
| **DENTIST** | `dr.perera` | `dentist123` | Dentist Workspace, Patient 360° Profile, FDI Tooth Chart, Clinical Records, Rx |
| **DENTIST** | `dr.silva` | `dentist123` | Dentist Workspace, Patient 360° Profile, FDI Tooth Chart, Clinical Records, Rx |

---

## 6. Installation & Execution Guide

### Prerequisites
* Java Development Kit (JDK 17 or higher)
* Apache Maven (version 3.8+)
* MySQL Server (e.g. XAMPP MySQL running on port 3306)

### Step 1: Database Setup
1. Start your local MySQL server (XAMPP Control Panel $\rightarrow$ Start MySQL).
2. Execute the database DDL schema and master seed data:
   * Run [`database/schema.sql`](file:///c:/Users/gihan/Desktop/Sunrise%20Dental%20Clinic/database/schema.sql) in MySQL Workbench or phpMyAdmin.
   * Run [`database/seed.sql`](file:///c:/Users/gihan/Desktop/Sunrise%20Dental%20Clinic/database/seed.sql) to populate initial users, dentists, treatments, medications, suppliers, and consumable inventory.

### Step 2: Build & Test
```powershell
mvn clean test
```
*All 29 unit and integration tests will execute against an in-memory H2 database.*

### Step 3: Run Application
```powershell
mvn spring-boot:run
```
*Application boots on `http://localhost:8085`.*

### Step 4: Access Interfaces
* **Web Application:** `http://localhost:8085` (or open static HTML files directly)
* **OpenAPI / Swagger UI:** `http://localhost:8085/swagger-ui.html`
* **API Documentation:** `http://localhost:8085/v3/api-docs`

---

## 7. Key API Endpoints Reference

### Appointments & Queue
* `GET /api/appointments/queue/today?date=YYYY-MM-DD` - Daily token queue with chair status
* `POST /api/appointments/{id}/queue/call` - Call patient from waiting area (`CALLED`)
* `POST /api/appointments/{id}/queue/start` - Start procedure in chair (`IN_TREATMENT`)
* `POST /api/appointments/{id}/queue/complete` - Complete visit (`COMPLETED`)
* `POST /api/appointments/{id}/queue/no-show` - Mark no-show (`NO_SHOW`)
* `GET /api/appointments/{id}/no-show-risk` - AI no-show risk score, factors, and recommendation

### Dental Chart & Clinical Records
* `GET /api/dental-chart/patient/{patientId}` - 32 FDI teeth grouped into 4 quadrants
* `PUT /api/dental-chart/tooth` - Update tooth condition and log transition history
* `GET /api/dental-chart/tooth/{toothId}/history` - History of condition changes
* `POST /api/clinical-records` - Record new clinical examination (`CR-YYYY-XXXXXX`)
* `GET /api/clinical-records/patient/{patientId}` - Chronological examination history
* `POST /api/diagnoses` - Record diagnosis linked to tooth and severity
* `POST /api/treatment-plans` - Create treatment plan with dynamic catalog pricing
* `PATCH /api/treatment-plans/{id}/status` - Update plan status (`APPROVED`, `COMPLETED`, etc.)

### Prescriptions & Formulary
* `GET /api/medications` - Active medication formulary
* `GET /api/medications/search?query=...` - Search formulary by brand or generic name
* `POST /api/prescriptions` - Issue new prescription (`RX-YYYY-XXXXXX`)
* `GET /api/prescriptions/{id}` - Prescription details for printable Rx letterhead

### Inventory, Suppliers & Purchase Orders
* `GET /api/inventory/items` - All consumable items with stock levels
* `GET /api/inventory/low-stock` - Items at or below reorder threshold
* `POST /api/inventory/adjust` - Record stock usage, wastage, return, or manual audit
* `POST /api/purchases` - Create purchase order (`PO-YYYY-XXXXXX`)
* `PATCH /api/purchases/{id}/status?status=RECEIVED` - **Goods Receipt**: automatically increments inventory stock

### Documents & Notifications
* `POST /api/documents/upload` - Upload X-ray, lab report, or consent form (multipart)
* `GET /api/documents/{id}/download` - Stream or download medical document binary
* `GET /api/notifications` - In-app notification feed for authenticated user
* `PATCH /api/notifications/{id}/read` - Mark notification as read
