package com.sunrise.dental.entity;

import com.sunrise.dental.enums.DocumentType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_documents")
public class PatientDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_record_id")
    private ClinicalRecord clinicalRecord;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 50)
    private DocumentType documentType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    @Column(name = "file_type", length = 100)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "uploaded_by", nullable = false, length = 50)
    private String uploadedBy;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public PatientDocument() {
    }

    public PatientDocument(Patient patient, ClinicalRecord clinicalRecord, String title, DocumentType documentType,
                           String fileName, String storedFileName, String fileType, Long fileSize,
                           String filePath, String notes, String uploadedBy) {
        this.patient = patient;
        this.clinicalRecord = clinicalRecord;
        this.title = title;
        this.documentType = documentType;
        this.fileName = fileName;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.notes = notes;
        this.uploadedBy = uploadedBy;
    }

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public ClinicalRecord getClinicalRecord() {
        return clinicalRecord;
    }

    public void setClinicalRecord(ClinicalRecord clinicalRecord) {
        this.clinicalRecord = clinicalRecord;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
