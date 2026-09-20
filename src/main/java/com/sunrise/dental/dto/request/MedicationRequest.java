package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.NotBlank;

public class MedicationRequest {

    @NotBlank(message = "Medication name is required")
    private String name;

    private String genericName;

    @NotBlank(message = "Dosage form is required")
    private String dosageForm;

    private String defaultDosage;

    private String defaultFrequency;

    private String instructions;

    private Boolean isActive = true;

    public MedicationRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getDefaultDosage() {
        return defaultDosage;
    }

    public void setDefaultDosage(String defaultDosage) {
        this.defaultDosage = defaultDosage;
    }

    public String getDefaultFrequency() {
        return defaultFrequency;
    }

    public void setDefaultFrequency(String defaultFrequency) {
        this.defaultFrequency = defaultFrequency;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
