package com.sunrise.dental.entity;

import com.sunrise.dental.enums.SessionStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_sessions")
public class TreatmentSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_plan_id", nullable = false)
    private TreatmentPlan treatmentPlan;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(name = "session_number", nullable = false)
    private Integer sessionNumber;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "completed_treatment")
    private String completedTreatment;

    @Column(name = "related_teeth", length = 100)
    private String relatedTeeth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TreatmentSession() {
    }

    public TreatmentSession(TreatmentPlan treatmentPlan, Patient patient, Dentist dentist, Appointment appointment,
                            Integer sessionNumber, LocalDate sessionDate, String completedTreatment,
                            String relatedTeeth, SessionStatus status, String notes) {
        this.treatmentPlan = treatmentPlan;
        this.patient = patient;
        this.dentist = dentist;
        this.appointment = appointment;
        this.sessionNumber = sessionNumber;
        this.sessionDate = sessionDate;
        this.completedTreatment = completedTreatment;
        this.relatedTeeth = relatedTeeth;
        this.status = status != null ? status : SessionStatus.SCHEDULED;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.sessionDate == null) {
            this.sessionDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = SessionStatus.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TreatmentPlan getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(TreatmentPlan treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getCompletedTreatment() {
        return completedTreatment;
    }

    public void setCompletedTreatment(String completedTreatment) {
        this.completedTreatment = completedTreatment;
    }

    public String getRelatedTeeth() {
        return relatedTeeth;
    }

    public void setRelatedTeeth(String relatedTeeth) {
        this.relatedTeeth = relatedTeeth;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
