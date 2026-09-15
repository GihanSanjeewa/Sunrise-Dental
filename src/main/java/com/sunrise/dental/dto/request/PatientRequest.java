package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public class PatientRequest {

    @NotBlank(message = "Patient full name is required.")
    private String fullName;

    @NotBlank(message = "Residential address is required.")
    private String address;

    @NotBlank(message = "Contact phone number is required.")
    @Pattern(regexp = "^[0-9+ -]{9,15}$", message = "Invalid phone number format.")
    private String contactNumber;

    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotNull(message = "Date of birth is required.")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required.")
    private String gender;

    public PatientRequest() {
    }

    public PatientRequest(String fullName, String address, String contactNumber, String email, LocalDate dateOfBirth, String gender) {
        this.fullName = fullName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
