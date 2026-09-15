# Sunrise Dental Clinic Appointment and Patient Management System
## Comprehensive University Software Engineering Project Report

---

### 1. Introduction
The modern healthcare sector is increasingly reliant upon distributed software systems to ensure patient safety, eliminate scheduling conflicts, and deliver accurate financial management. The **Sunrise Dental Clinic Appointment and Patient Management System** is a distributed enterprise application engineered to transition an established dental practice in Colombo from error-prone manual paper registers to a digital, secure, and robust platform. This report documents the complete systems development lifecycle (SDLC) of the application, encompassing architectural design, test-driven development (TDD), object-oriented design patterns, database normalization, security enforcement, and automated quality assurance.

### 2. Background
Sunrise Dental Clinic operates as an outpatient facility in Colombo, Sri Lanka, providing multidisciplinary dental services including general consultations, scaling, restorations, surgical extractions, endodontics (root canal treatments), prosthetics, and cosmetic whitening. Prior to this project, front-desk personnel utilized handwritten paper notebooks to log patient appointments, manual index cards to record medical histories, and paper receipts for financial payments.

### 3. Problem Identification
Manual operational workflows generated significant organizational bottlenecks:
* **Double-Booking Collisions:** Lack of concurrency control allowed multiple receptionists to allocate the same dentist to overlapping appointment times.
* **Misplaced & Inaccessible Records:** Physical patient index cards were prone to wear, loss, and filing errors, depriving dentists of critical prior clinical notes.
* **Billing Discrepancies:** Manual arithmetic calculation of treatment costs, consultation fees, promotional discounts, and statutory taxes resulted in financial losses and invoicing disputes.
* **Lack of Accountability:** Paper systems lacked immutable audit trails, making it impossible to identify staff responsible for scheduling errors.

### 4. Proposed Solution
The proposed computerized solution is a distributed, multi-tier web application built upon Java 17, Spring Boot 3, Spring Data JPA, and MySQL. It introduces:
* Atomic double-booking conflict prevention at both the business and database layer.
* Dynamic, database-driven treatment pricing (guaranteeing zero hardcoded prices).
* Automated billing calculations with printable official receipts.
* Role-based access control (RBAC) separating administrative, reception, and dental provider operations.
* A responsive Bootstrap 5 client interface with real-time KPI dashboards and analytical reports.

### 5. Objectives
1. Eliminate 100% of dentist double-booking scheduling conflicts.
2. Automate unique sequence generation for Patients (`P-XXXXXX`), Dentists (`D-XXXXXX`), Appointments (`APT-YYYY-XXXXXX`), Bills (`BILL-YYYY-XXXXXX`), and Payments (`PAY-YYYY-XXXXXX`).
3. Enforce cryptographic password security using BCrypt salted hashing.
4. Separate external API data transfer objects (DTOs) from internal JPA domain entities.
5. Demonstrate Test-Driven Development (TDD) through Red-Green-Refactor cycles.
6. Provide interactive OpenAPI / Swagger documentation for distributed service integration.

### 6. Scope
The system encompasses:
* **In Scope:** User authentication; patient onboarding and directory management; dentist roster and availability tracking; treatment pricing catalogs; appointment booking, searching, updating, and cancellation; dynamic billing calculations; payment settlement; browser receipt printing; operational reporting; audit logging.
* **Out of Scope:** Third-party payment gateway integration (e.g., live banking card acquirers), automated SMS/email gateways, and 3D dental radiological imaging storage.

### 7. Functional Requirements
* **FR1 (Authentication):** The system shall authenticate staff via username and BCrypt-hashed password, granting access based on `ADMIN`, `RECEPTIONIST`, or `DENTIST` roles.
* **FR2 (Patient Management):** The system shall register patients with validated demographic data and assign a unique sequential number (`P-000001`).
* **FR3 (Dentist Management):** The system shall maintain dentist profiles, clinical specialties, and availability states (`AVAILABLE`, `ON_LEAVE`, `INACTIVE`).
* **FR4 (Treatment Catalog):** The system shall manage treatment procedures, baseline costs, and consultation fees pulled dynamically from the database.
* **FR5 (Appointment Conflict Prevention):** The system shall prevent booking any appointment if the target dentist has an active appointment at the requested date and time slot, returning HTTP 409 Conflict: *"Selected dentist is already booked for this time."*
* **FR6 (Appointment Search):** The system shall provide direct search by unique appointment number as well as multi-criteria filtering by patient, dentist, date, and status.
* **FR7 (Billing Engine):** The system shall compute subtotal, configurable discounts, taxes, and net totals from database values.
* **FR8 (Payment & Receipts):** The system shall process cash, card, and bank transfer payments and format official browser-printable receipts.
* **FR9 (Reporting & Dashboard):** The system shall render real-time dashboard KPIs and generate daily/monthly schedule and financial revenue reports.

### 8. Non-Functional Requirements
* **Security:** Role-based endpoint authorization, BCrypt salted hashing, SQL injection prevention via prepared JPA statements, zero password leakage in DTOs.
* **Performance:** Sub-second API response times ($< 500\text{ ms}$) enabled by database indexes on `appointment_number`, `patient_number`, `dentist_id`, and `appointment_date`.
* **Data Integrity:** ACID transactional consistency enforced via Spring `@Transactional` boundaries.
* **Usability:** Responsive, intuitive Bootstrap 5 interface tailored for non-technical staff with clear validation alerts.
* **Maintainability:** Modular layered architecture following enterprise Java conventions.

### 9. Assumptions
1. The system operates on the clinic's local network or private cloud environment.
2. Standard clinical appointments are scheduled in fixed 30-minute time intervals.
3. Each appointment binds exactly one patient, one dentist, and one treatment procedure.
4. A cancelled appointment immediately releases the scheduled time slot for re-booking.
5. All consultation fees, treatment costs, and tax percentages are database-driven to avoid code modification.

### 10. System Architecture
The application employs a 4-tier distributed layered architecture:
```
Presentation Tier (HTML5, CSS3, JavaScript Fetch API, Bootstrap 5)
                                 ↕ HTTP / JSON
Web Services Tier (Spring Boot REST Controllers, Swagger OpenAPI)
                                 ↕ DTOs / Method Calls
Service & Business Logic Tier (Service Interfaces, TDD Conflict Engine, Strategies, Factories)
                                 ↕ JPA Entities
Persistence Tier (Spring Data JPA, Hibernate ORM)
                                 ↕ SQL
Database Tier (MySQL 8.x Relational Database with Constraints & Indexes)
```

### 11. Use Case Diagram
The Use Case diagram features three primary actors (`Admin`, `Receptionist`, `Dentist`) interacting with subsystems across Authentication, Patient Management, Appointment Scheduling, Billing, Catalog Management, and Reporting. Key relationships include:
* `Schedule Appointment` $\ll include \gg$ `Validate Slot Conflict`
* `Schedule Appointment` $\ll include \gg$ `Verify Dentist Availability`
* `Generate Bill` $\ll include \gg$ `Calculate Itemized Charges`
* `Print Receipt` $\ll extend \gg$ `Record Payment`
* `Cancel Appointment` $\ll extend \gg$ `Search Appointment`

### 12. Use Case Descriptions
* **UC-04 Schedule Appointment:** Preconditions: Patient registered, dentist active. Main Flow: User selects patient, dentist, procedure, date, and time slot. System verifies slot availability. If clear, persists appointment with status `BOOKED` and returns unique sequence number (`APT-2026-XXXXXX`). Exception Flow: If conflict detected, throws `AppointmentConflictException` and displays: *"Selected dentist is already booked for this time."*

### 13. Class Diagram
The class diagram documents the entity hierarchy (`User`, `Patient`, `Dentist`, `Treatment`, `Appointment`, `Bill`, `Payment`, `AuditLog`), associated enumerations (`Role`, `AppointmentStatus`, `PaymentStatus`, `PaymentMethod`, `DentistStatus`), repository abstractions, service interfaces, controller classes, and design pattern structures.

### 14. Sequence Diagrams
Detailed PlantUML sequence diagrams model:
1. User Authentication and session initialization.
2. Appointment booking with atomic double-booking conflict detection.
3. Direct search by appointment number.
4. Dynamic bill generation and fee calculation.
5. Payment execution via Strategy pattern and receipt assembly via Factory pattern.

### 15. Database Design
The relational schema comprises 8 normalized tables adhering to Third Normal Form (3NF):
* `users`, `patients`, `dentists`, `treatments`, `appointments`, `bills`, `payments`, and `audit_logs`.
* Constraints include foreign keys with `ON DELETE RESTRICT` to preserve medical auditability, unique constraints on operational identifiers, and composite indexes on `(dentist_id, appointment_date, appointment_time, status)`.

### 16. Entity-Relationship (ER) Diagram
The data model formalizes domain relationships:
* Patient $1 \longleftrightarrow *$ Appointment
* Dentist $1 \longleftrightarrow *$ Appointment
* Treatment $1 \longleftrightarrow *$ Appointment
* Appointment $1 \longleftrightarrow 1$ Bill
* Bill $1 \longleftrightarrow *$ Payment

### 17. Design Patterns
1. **Model-View-Controller (MVC):** Decouples client presentation from REST controller endpoints and service models.
2. **Repository Pattern:** Abstracts persistence operations using Spring Data JPA.
3. **Service Layer Pattern:** Houses transaction boundaries (`@Transactional`) and business logic.
4. **Data Transfer Object (DTO) Pattern:** Prevents entity over-exposure and JSON recursion.
5. **Factory Pattern (`ReceiptFactory`):** Encapsulates the multi-step composition of formatted, printable official receipts.
6. **Strategy Pattern (`PaymentStrategy`):** Decouples payment validation behaviors across `CashPaymentStrategy`, `CardPaymentStrategy`, and `BankTransferPaymentStrategy`.
7. **Singleton Pattern:** Enforced via Spring's IoC container managing thread-safe stateless bean instances.

### 18. UI Design
The frontend adheres to modern clinical design principles utilizing a palette of Medical Teal (`#0d9488`), Sunrise Accent (`#f59e0b`), and Slate (`#0f172a`). A persistent responsive sidebar provides streamlined navigation across Dashboard, Appointments, Patients, Invoicing, Dentists, Treatments, Reports, Help, and Users.

### 19. Implementation
The backend is implemented using Java 17 LTS and Spring Boot 3.2.4. REST controllers map request payloads to DTOs, invoke transaction-safe service implementations, and format structured JSON responses. The presentation layer employs vanilla ES6 Fetch API communicating asynchronously with the backend.

### 20. Validation
Multi-tier input validation is enforced:
* Client-side HTML5 constraint validation.
* Backend Jakarta Bean Validation annotations (`@NotBlank`, `@NotNull`, `@Positive`, `@FutureOrPresent`, `@Pattern`).
* Business-tier transactional validation verifying slot availability, account active statuses, and duplicate records.

### 21. Security
* **Password Hashing:** Passwords are encrypted using BCrypt with a cost factor of 10.
* **Role-Based Authorization:** Endpoints restrict administrative actions (such as treatment pricing and user management) to `ADMIN` accounts.
* **SQL Injection & XSS Mitigation:** Parameterized queries via Hibernate prevent SQL injection; DTO serialization sanitizes output.

### 22. Testing Strategy
A comprehensive testing pyramid combines TDD for business logic, isolated unit testing with JUnit 5 and Mockito, and full-stack integration testing with Spring Boot Test and MockMvc against an in-memory H2 database.

### 23. Test-Driven Development (TDD) Process
TDD was demonstrated for the core double-booking prevention feature:
* **RED Phase:** Authored `shouldRejectAppointmentWhenDentistAlreadyBooked()` in `AppointmentServiceTest` asserting that an `AppointmentConflictException` is thrown when an overlapping appointment exists. The test failed as expected.
* **GREEN Phase:** Implemented `existsActiveSlotForDentist` query in `AppointmentRepository` and added the conflict check in `AppointmentServiceImpl`. The test passed.
* **REFACTOR Phase:** Extracted helper validation routines and added composite database index `idx_apt_conflict_check`. All tests remained green.

### 24. Test Plan
A master test plan matrix encompassing 28 positive, negative, boundary, and security test cases was executed (see `docs/test-plan/master-test-plan.md`).

### 25. Test Results
Automated test execution via `mvn test` yielded:
* **Tests Run:** 15 unit and integration tests.
* **Failures:** 0.
* **Errors:** 0.
* **Skipped:** 0.
* **Build Status:** `BUILD SUCCESS`.

### 26. Git / GitHub Workflow
Development followed the GitFlow branching model:
* `main`: Stable releases tagged with semantic versioning (`v1.0.0`).
* `develop`: Integration branch.
* Feature branches: `feature/auth-security`, `feature/patient-management`, `feature/appointment-scheduling`, `feature/billing-payment`, `feature/testing-suite`.

### 27. Version Control History
The repository features an incremental 20-commit history reflecting daily, structured development milestones conforming to academic evaluation guidelines.

### 28. Challenges Encountered
1. Ensuring atomic double-booking prevention during concurrent receptionist booking requests.
2. Dynamically calculating treatment bills without hardcoding medical procedure fees in Java code.
3. Implementing printable official receipts that conform to browser print stylesheets without third-party PDF bloat.

### 29. Solutions Implemented
1. Combined JPA repository verification queries with composite database indexing on `(dentist_id, appointment_date, appointment_time, status)`.
2. Encapsulated pricing logic in a dynamic `BillingService` deriving prices from the `Treatment` entity.
3. Utilized CSS `@media print` rules and a dedicated `ReceiptFactory` to format printable receipts directly from the DOM.

### 30. Limitations
* The current system does not integrate with external SMS telecommunications gateways for automated patient appointment reminders.
* Payment processing simulates card and bank transfers via strategy validation rather than connecting to live merchant acquirer APIs.

### 31. Future Improvements
* Integration of SMS/WhatsApp notification gateways via Twilio.
* Patient self-service booking portal and online payment gateway integration (Stripe / PayHere).
* Cloud object storage for digital dental X-rays and imaging records.

### 32. Conclusion
The Sunrise Dental Clinic Appointment and Patient Management System fulfills all functional and academic requirements established by the university assignment. By leveraging modern Java enterprise standards, robust object-oriented design patterns, strict database normalization, and test-driven validation, the system successfully transitions the clinic into a paperless, efficient, and secure healthcare environment.

---

### 33. References (Harvard Format)

* Gamma, E., Helm, R., Johnson, R. and Vlissides, J., 1994. *Design Patterns: Elements of Reusable Object-Oriented Software*. Reading, MA: Addison-Wesley.
* Martin, R.C., 2008. *Clean Code: A Handbook of Agile Software Craftsmanship*. Upper Saddle River, NJ: Prentice Hall.
* Walls, C., 2022. *Spring in Action*. 6th ed. Shelter Island, NY: Manning Publications.
* Fowler, M., 2002. *Patterns of Enterprise Application Architecture*. Boston, MA: Addison-Wesley.
* Freeman, E. and Robson, E., 2020. *Head First Design Patterns*. 2nd ed. Sebastopol, CA: O'Reilly Media.
* Beck, K., 2003. *Test-Driven Development: By Example*. Boston, MA: Addison-Wesley.
* Silberschatz, A., Korth, H.F. and Sudarshan, S., 2019. *Database System Concepts*. 7th ed. New York: McGraw-Hill.
