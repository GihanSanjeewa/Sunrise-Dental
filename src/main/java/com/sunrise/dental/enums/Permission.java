package com.sunrise.dental.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Granular system permissions for Role-Based and Permission-Based Access Control.
 */
public enum Permission {
    // Patient Management
    PATIENT_READ("Patient Management", "View patient demographics, contact info, and medical summaries"),
    PATIENT_WRITE("Patient Management", "Create, edit, and update patient profiles"),

    // Appointments & Queue
    APPOINTMENT_READ("Appointments & Queue", "View appointments calendar, daily roster, and queue list"),
    APPOINTMENT_WRITE("Appointments & Queue", "Book, reschedule, cancel appointments and manage queue calling"),

    // Clinical & Dental Chart
    CLINICAL_READ("Clinical Records", "View patient clinical notes, medical history, and allergies"),
    CLINICAL_WRITE("Clinical Records", "Record patient visits, clinical diagnoses, and treatment notes"),
    CHART_READ("Dental Chart", "View interactive 32-tooth FDI dental chart and tooth history"),
    CHART_WRITE("Dental Chart", "Update tooth conditions (caries, crowns, etc.) and record tooth procedures"),

    // Treatments & Prescriptions
    TREATMENT_PLAN_READ("Treatment Plans", "View patient treatment plans, cost estimates, and session progression"),
    TREATMENT_PLAN_WRITE("Treatment Plans", "Create and approve treatment plans and conduct treatment sessions"),
    PRESCRIPTION_READ("Prescriptions", "View issued drug prescriptions and dosage instructions"),
    PRESCRIPTION_WRITE("Prescriptions", "Issue and print medical prescriptions from the medication catalog"),

    // Billing & Finance
    BILLING_READ("Billing & Payments", "View patient invoices, payment records, and financial receipts"),
    BILLING_WRITE("Billing & Payments", "Generate bills, apply discounts, and record cash/card/insurance payments"),

    // Inventory & Supplies
    INVENTORY_READ("Inventory & Supplies", "View clinic inventory stock levels, item catalog, and suppliers"),
    INVENTORY_WRITE("Inventory & Supplies", "Perform stock adjustments, register suppliers, and issue purchase orders"),

    // System & Administration
    REPORTS_VIEW("Reports & Analytics", "Access clinic analytics, revenue reports, and doctor utilization charts"),
    USER_MANAGE("User Administration", "Create and manage staff user accounts, reset passwords, and assign permissions");

    private final String category;
    private final String description;

    Permission(String category, String description) {
        this.category = category;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Resolves default permissions for a given system role.
     */
    public static Set<Permission> getDefaultPermissions(Role role) {
        if (role == null) {
            return Collections.emptySet();
        }
        switch (role) {
            case ADMIN:
                return new HashSet<>(Arrays.asList(values()));
            case RECEPTIONIST:
                return new HashSet<>(Arrays.asList(
                        PATIENT_READ, PATIENT_WRITE,
                        APPOINTMENT_READ, APPOINTMENT_WRITE,
                        BILLING_READ, BILLING_WRITE,
                        REPORTS_VIEW
                ));
            case DENTIST:
                return new HashSet<>(Arrays.asList(
                        PATIENT_READ,
                        APPOINTMENT_READ,
                        CLINICAL_READ, CLINICAL_WRITE,
                        CHART_READ, CHART_WRITE,
                        TREATMENT_PLAN_READ, TREATMENT_PLAN_WRITE,
                        PRESCRIPTION_READ, PRESCRIPTION_WRITE,
                        INVENTORY_READ
                ));
            default:
                return Collections.emptySet();
        }
    }
}
