# Sunrise Dental Clinic Appointment and Patient Management System

[![Java](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/Database-MySQL%208.x-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-Academic%20Project-lightgrey.svg)]()

> A complete, secure, test-driven, distributed enterprise application engineered for **Sunrise Dental Clinic** in Colombo, Sri Lanka. Built to digitize clinical records, prevent double-bookings, automate database-driven billing, and generate printable official receipts.

---

## 1. Project Overview & Business Problem

Sunrise Dental Clinic is a busy multi-practitioner dental care center in Colombo. Prior manual paper registers caused critical operational bottlenecks:
* Overlapping appointment slots (double-bookings) for attending dentists.
* Misplaced patient history and clinical notes.
* Arithmetic inaccuracies in billing, discount, and tax calculations.
* Lack of audit accountability and management revenue visibility.

This system delivers a robust, distributed client-server architecture with REST web services, role-based access control, dynamic database-driven pricing, and an interactive Bootstrap 5 frontend.

---

## 2. Key System Features

* **Authentication & RBAC:** Salted BCrypt password encryption, session token authentication, role-based access for `ADMIN`, `RECEPTIONIST`, and `DENTIST`.
* **Patient Management:** Auto-generated sequence IDs (`P-000001`), demographic validations, contact uniqueness checks, full CRUD, and chronological medical history.
* **Dentist Management:** Roster tracking, specialization assignment, availability toggles (`AVAILABLE`, `ON_LEAVE`, `INACTIVE`), and schedule lookups.
* **Treatment Catalog (Zero Hardcoding):** Procedure catalog where consultation fees and treatment costs are pulled dynamically from the database.
* **Atomic Double-Booking Prevention:** Atomic verification queries preventing dentists from being scheduled for overlapping active slots (`BOOKED`/`CONFIRMED`). Throws structured HTTP 409 Conflict: *"Selected dentist is already booked for this time."*
* **Appointment Number Search:** Direct lookup by unique identifier (`APT-2026-000001`) rendering comprehensive patient, dentist, treatment, date, time, and clinical status details.
* **Dynamic Billing Engine:** Calculates invoices using:
  $$\text{Subtotal} = \text{Consultation Fee} + \text{Treatment Cost}$$
  $$\text{Total} = \text{Subtotal} - \text{Discount} + \text{Tax}$$
* **Payment Processing (Strategy Pattern):** Support for `CASH`, `CARD`, and `BANK_TRANSFER` methods.
* **Printable Receipts (Factory Pattern):** Browser-printable receipts formatted with clinic header, itemized charges, and clinic footer note.
* **Executive Dashboard & Analytics:** Real-time KPI summary cards and 8 multi-dimensional reports with date filters, print preview, and CSV export.
* **Interactive Help Manual:** 11-step standard operating procedure (SOP) guide for clinic staff.
* **Audit Trail Ledger:** Immutable record of all system events and operations.

---

## 3. Technology Stack

* **Backend:** Java 17 LTS, Spring Boot 3.2.4, Spring Web (REST API), Spring Data JPA, Hibernate ORM, Maven 3.9.6.
* **Database:** MySQL 8.x / MariaDB (XAMPP compatible).
* **Testing:** JUnit 5, Mockito, Spring Boot Test, MockMvc, H2 in-memory test database.
* **API Documentation:** Springdoc OpenAPI / Swagger UI 2.3.0.
* **Frontend:** HTML5, CSS3 (Modern Medical Design System), JavaScript (ES6 Fetch API), Bootstrap 5.3.3.
* **Security:** Spring Security, BCrypt (strength 10).

---

## 4. Software Architecture & Design Patterns

The project enforces a clean 4-tier layered architecture:
```
Presentation (Bootstrap 5 / Vanilla JS Fetch)
      ↓
Web Services / REST API (Spring Controllers)
      ↓
Service Layer (Business Rules & Transactional Boundaries)
      ↓
Repository Layer (Spring Data JPA / Hibernate)
      ↓
Relational Database (MySQL 8.x)
```

### Design Patterns Implemented:
1. **MVC Pattern:** Strict decoupling between presentation templates, REST controllers, and domain models.
2. **Repository Pattern:** Abstraction of persistence logic via `JpaRepository` interfaces.
3. **Service Layer Pattern:** Encapsulation of transaction management and business conflict validation.
4. **Data Transfer Object (DTO) Pattern:** Decoupling internal JPA entities from external API contracts.
5. **Factory Pattern (`ReceiptFactory`):** Centralized assembly of printable receipts from bills and payments.
6. **Strategy Pattern (`PaymentStrategy`):** Encapsulates interchangeable payment processing strategies (`CashPaymentStrategy`, `CardPaymentStrategy`, `BankTransferPaymentStrategy`).
7. **Singleton Pattern:** Managed singleton bean lifecycles via Spring Inversion of Control (IoC).

---

## 5. Default Test Credentials

| Role | Username | Default Password | Permissions |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` | Full access: Users, Treatments, Dentists, Reports, System Audits |
| **RECEPTIONIST** | `receptionist` | `recept123` | Patients, Appointments, Billing, Payments, Receipts, Reports |
| **DENTIST** | `dr.perera` | `dentist123` | View assigned appointments, clinical notes, patient treatment history |
| **DENTIST** | `dr.silva` | `dentist123` | View assigned appointments, clinical notes, patient treatment history |

---

## 6. Installation & Execution Guide

### Prerequisites
* Java Development Kit (JDK 17 or higher)
* Apache Maven (version 3.8+)
* MySQL Server (e.g. XAMPP MySQL running on port 3306)

### Step 1: Database Setup
1. Start your local MySQL server (XAMPP Control Panel $\rightarrow$ Start MySQL).
2. Execute the database DDL schema and master seed data:
```bash
mysql -u root < database/schema.sql
mysql -u root < database/seed.sql
```

### Step 2: Configure Environment Variables
Copy `.env.example` to `.env` or verify `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sunrise_dental_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
```

### Step 3: Run the Spring Boot Application
```bash
mvn spring-boot:run
```
The application will start on port `8080`.

### Step 4: Access the Application
* **Frontend Web Application:** Open your browser and navigate to:
  `http://localhost:8080/index.html`
* **Swagger / OpenAPI Documentation:**
  `http://localhost:8080/swagger-ui.html`
* **Raw OpenAPI JSON Spec:**
  `http://localhost:8080/api-docs`

---

## 7. Automated Testing Suite

To execute the automated unit and integration tests (including the TDD double-booking prevention verification):
```bash
# Run unit and integration tests
mvn test

# Run complete verification and packaging
mvn verify
```

### Test Coverage Highlights
* `AppointmentIntegrationTest`: End-to-end booking verification, double-booking rejection (409 Conflict), and search lookups.
* `AppointmentServiceTest`: Double-booking conflict logic, slot release on cancellation, past date rejection.
* `BillingServiceTest`: Subtotal, discount, tax, and net total calculation tests.
* `PatientServiceTest`: Demographic validation, unique number generation, duplicate rejection.
* `AuthServiceTest`: BCrypt authentication and role verification.

---

## 8. Git & GitHub Version Control Workflow

The project follows a structured GitFlow branching strategy:
* `main`: Stable production releases tagged with semantic versioning (`v1.0.0`).
* `develop`: Integration branch for aggregating completed features.
* Feature branches (`feature/auth`, `feature/appointments`, `feature/billing`, `feature/testing`).

---

## 9. University Information & Credits
* **Module:** Distributed Systems & Software Engineering
* **Project:** Sunrise Dental Clinic Appointment and Patient Management System
* **Academic Year:** 2026
