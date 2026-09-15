package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.DentistStatus;
import java.time.LocalDateTime;

public class DentistResponse {
    private Long id;
    private String dentistNumber;
    private String name;
    private String specialization;
    private String contactNumber;
    private String email;
    private DentistStatus availabilityStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DentistResponse() {
    }

    public DentistResponse(Long id, String dentistNumber, String name, String specialization, String contactNumber,
                           String email, DentistStatus availabilityStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.dentistNumber = dentistNumber;
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
        this.availabilityStatus = availabilityStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDentistNumber() {
        return dentistNumber;
    }

    public void setDentistNumber(String dentistNumber) {
        this.dentistNumber = dentistNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DentistStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(DentistStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
