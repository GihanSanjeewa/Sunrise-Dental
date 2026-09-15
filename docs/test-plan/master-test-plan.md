# Sunrise Dental Clinic Appointment and Patient Management System
## Master Software Test Plan (IEEE 829 Compliant)

---

### 1. Test Strategy & Scope
This test plan establishes the verification and validation (V&V) procedures for the **Sunrise Dental Clinic Appointment and Patient Management System**. The testing strategy adopts a multi-tiered approach:
* **Test-Driven Development (TDD):** Red $\rightarrow$ Green $\rightarrow$ Refactor cycle for critical business logic (double-booking prevention, dynamic bill calculation, unique sequence generation).
* **Automated Unit Testing:** JUnit 5 and Mockito isolating service classes and boundary conditions.
* **Automated Integration Testing:** Spring Boot Test (`@SpringBootTest`) and `MockMvc` executing end-to-end HTTP web service invocations against an in-memory MySQL-compatible H2 database.
* **Manual User Acceptance Testing (UAT):** Verifying UI responsiveness, receipt printing fidelity, and non-technical staff workflows.

---

### 2. Test Plan Matrix (28 Comprehensive Test Scenarios)

| Test ID | Test Scenario | Test Input / Data | Execution Layer | Expected Result | Actual Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC001** | Valid Admin Login | Username: `admin`, Password: `admin123` | Unit / Controller | HTTP 200 OK, returns session token and Admin role | 200 OK with token | **PASS** |
| **TC002** | Invalid Login Credentials | Username: `admin`, Password: `wrongpassword` | Unit / Controller | HTTP 401 Unauthorized, "Invalid username or password." | 401 Unauthorized | **PASS** |
| **TC003** | Blank Username Validation | Username: `""`, Password: `admin123` | Unit (Bean Validation) | HTTP 400 Bad Request, "Please enter your username." | 400 Bad Request | **PASS** |
| **TC004** | Blank Password Validation | Username: `admin`, Password: `""` | Unit (Bean Validation) | HTTP 400 Bad Request, "Please enter your password." | 400 Bad Request | **PASS** |
| **TC005** | Deactivated User Account Login | Username: `inactive_user` (Active=False) | Service / Unit | HTTP 401 Unauthorized, account deactivated message | 401 Unauthorized | **PASS** |
| **TC006** | Register Patient with Valid Data | Name: "Kamal Silva", Phone: "0712345678", DOB: 1988-05-20 | Unit & Integration | HTTP 201 Created, auto-assigns next ID `P-000006` | 201 Created (`P-000006`) | **PASS** |
| **TC007** | Register Patient with Blank Name | Name: `""`, Phone: "0712345678" | Unit (Validation) | HTTP 400 Bad Request, "Patient name cannot be empty." | 400 Bad Request | **PASS** |
| **TC008** | Register Patient with Invalid Phone | Phone: "abc12345" | Bean Validation | HTTP 400 Bad Request, "Invalid phone number format." | 400 Bad Request | **PASS** |
| **TC009** | Reject Duplicate Patient Phone | Existing Phone: "0771234567" | Service / Unit | HTTP 409 Conflict, "A patient with contact number..." | 409 Conflict | **PASS** |
| **TC010** | Search Patient by Name Keyword | Query: "Sunil" | Service / Repository | Returns list containing Sunil Wickramasinghe | Patient retrieved | **PASS** |
| **TC011** | Create Appointment in Open Slot | Patient: 1, Dentist: 1, Date: Tomorrow, Time: 10:00 | Integration (`MockMvc`) | HTTP 201 Created, auto-assigns `APT-2026-XXXXXX` | 201 Created | **PASS** |
| **TC012** | **TDD: Atomic Double Booking Prevention** | Same Dentist (1), Same Date, Same Time (10:00) | Integration & Unit | **HTTP 409 Conflict**, "Selected dentist is already booked for this time." | **409 Conflict Rejected** | **PASS** |
| **TC013** | Reject Appointment in Past Date | Date: Yesterday (2026-09-14) | Service Validation | HTTP 400 Bad Request, "Appointment cannot be created in an invalid past date." | 400 Bad Request | **PASS** |
| **TC014** | Cancelled Appointment Releases Slot | Status transition $\rightarrow$ `CANCELLED` | Service & Integration | Original time slot accepts new booking without conflict | Slot re-bookable | **PASS** |
| **TC015** | Direct Search by Appointment Number | `APT-2026-000001` | Integration / Rest | HTTP 200 OK, returns full patient, dentist, treatment payload | 200 OK | **PASS** |
| **TC016** | Search Non-Existent Appointment Number | `APT-9999-999999` | Integration / Rest | HTTP 404 Not Found, "No appointment found with the provided appointment number." | 404 Not Found | **PASS** |
| **TC017** | Dynamic Pricing Calculation | Fee: 2000, Cost: 6000, Disc: 5%, Tax: 2.5% | Service Unit Test | Subtotal: 8000, Disc: 400, Tax: 190, Net Total: 7790.00 | Exact match 7790.00 | **PASS** |
| **TC018** | Zero Hardcoded Price Guarantee | Modify Treatment Cost in DB | Service / ORM | Bill calculation automatically derives updated database cost | DB value used | **PASS** |
| **TC019** | Reject Duplicate Bill Generation | Same appointment ID booked twice | Service / Repository | HTTP 409 Conflict, "A bill has already been generated..." | 409 Conflict | **PASS** |
| **TC020** | Record Valid Cash Payment in Full | Amount: 7790.00, Method: `CASH` | Service (Strategy) | Payment recorded, Bill status transitions to `PAID` | Status $\rightarrow$ PAID | **PASS** |
| **TC021** | Record Partial Payment | Amount: 3000.00 (< 7790.00) | Service / Repository | Payment recorded, Bill status $\rightarrow$ `PARTIALLY_PAID` | PARTIALLY_PAID | **PASS** |
| **TC022** | Reject Zero or Negative Payment | Amount: 0.00 or -500.00 | Validation / Strategy | HTTP 400 Bad Request, "Payment amount must be greater than zero." | 400 Bad Request | **PASS** |
| **TC023** | Card Payment Strategy Validation | Method: `CARD`, Valid Amount | Strategy Pattern | `CardPaymentStrategy` validates execution successfully | Strategy executed | **PASS** |
| **TC024** | Bank Transfer Payment Strategy | Method: `BANK_TRANSFER` | Strategy Pattern | `BankTransferPaymentStrategy` validates transaction | Strategy executed | **PASS** |
| **TC025** | Printable Receipt Generation | Completed Bill & Payment | Factory Pattern | `ReceiptFactory` produces formatted receipt with clinic header and footer | Formatted receipt | **PASS** |
| **TC026** | Daily Appointment Report Aggregation | Target Date: 2026-09-16 | Report Service | Returns scheduled appointments filtered for target date | Filtered list | **PASS** |
| **TC027** | Revenue Report Date Range Filter | From: 2026-09-01, To: 2026-09-30 | Report Service | Accurate sum of settled bills within date window | Accurate sum | **PASS** |
| **TC028** | Audit Trail Persistence | Book appointment action | Audit Log Entity | Record persisted with username, entity, timestamp, details | Log persisted | **PASS** |

---

### 3. TDD Evidence Summary
* **Feature:** Double Booking Conflict Prevention
  * *Red Phase:* authored test `shouldRejectAppointmentWhenDentistAlreadyBooked()` expecting `AppointmentConflictException`. Failed with assertion error.
  * *Green Phase:* Implemented repository query `existsActiveSlotForDentist()` and added conflict check in `AppointmentServiceImpl.createAppointment()`. Test passed.
  * *Refactor Phase:* Extracted validation method `validateAppointmentRequest()` and added composite index `idx_apt_conflict_check` in MySQL DDL. Verified all 15 tests green.
