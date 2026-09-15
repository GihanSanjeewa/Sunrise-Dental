package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.DentistStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DentistRequest {

    @NotBlank(message = "Dentist name is required.")
    private String name;

    @NotBlank(message = "Clinical specialization is required.")
    private String specialization;

    @NotBlank(message = "Contact phone number is required.")
    private String contactNumber;

    @NotBlank(message = "Email address is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotNull(message = "Availability status is required.")
    private DentistStatus availabilityStatus;

    public DentistRequest() {
    }

    public DentistRequest(String name, String specialization, String contactNumber, String email, DentistStatus availabilityStatus) {
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
        this.availabilityStatus = availabilityStatus;
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
}
