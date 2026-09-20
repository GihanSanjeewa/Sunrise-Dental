package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.response.PatientDocumentResponse;
import com.sunrise.dental.entity.ClinicalRecord;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.entity.PatientDocument;
import com.sunrise.dental.enums.DocumentType;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.ClinicalRecordRepository;
import com.sunrise.dental.repository.PatientDocumentRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.PatientDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientDocumentServiceImpl implements PatientDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(PatientDocumentServiceImpl.class);
    private final Path uploadDir = Paths.get("uploads/documents").toAbsolutePath().normalize();

    private final PatientDocumentRepository patientDocumentRepository;
    private final PatientRepository patientRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final AuditService auditService;

    public PatientDocumentServiceImpl(PatientDocumentRepository patientDocumentRepository,
                                      PatientRepository patientRepository,
                                      ClinicalRecordRepository clinicalRecordRepository,
                                      AuditService auditService) {
        this.patientDocumentRepository = patientDocumentRepository;
        this.patientRepository = patientRepository;
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.auditService = auditService;

        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            logger.error("Could not initialize document storage directory: {}", e.getMessage());
        }
    }

    @Override
    public PatientDocumentResponse uploadDocument(Long patientId, Long clinicalRecordId, String title,
                                                 DocumentType documentType, String notes,
                                                 MultipartFile file, String uploadedBy) {
        if (patientId == null) {
            throw new ValidationException("Patient ID is required.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Document title is required.");
        }
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be empty.");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        ClinicalRecord cr = null;
        if (clinicalRecordId != null) {
            cr = clinicalRecordRepository.findById(clinicalRecordId).orElse(null);
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.dat";
        // Sanitize filename and generate unique stored name
        String cleanOriginal = Paths.get(originalFilename).getFileName().toString();
        String storedFilename = UUID.randomUUID().toString() + "_" + cleanOriginal;
        Path targetLocation = this.uploadDir.resolve(storedFilename);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + cleanOriginal + ": " + e.getMessage(), e);
        }

        PatientDocument doc = new PatientDocument(
                patient,
                cr,
                title.trim(),
                documentType != null ? documentType : DocumentType.OTHER,
                cleanOriginal,
                storedFilename,
                file.getContentType(),
                file.getSize(),
                targetLocation.toString(),
                notes,
                uploadedBy != null ? uploadedBy : "STAFF"
        );

        PatientDocument saved = patientDocumentRepository.save(doc);
        auditService.logAction(uploadedBy != null ? uploadedBy : "STAFF", "UPLOAD", "PATIENT_DOCUMENT", saved.getId().toString(),
                "Uploaded " + saved.getDocumentType() + " '" + saved.getTitle() + "' for patient " + patient.getFullName());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDocumentResponse getDocumentById(Long id) {
        PatientDocument doc = patientDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));
        return mapToResponse(doc);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource loadFileAsResource(Long id) {
        PatientDocument doc = patientDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        try {
            Path filePath = this.uploadDir.resolve(doc.getStoredFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found on disk: " + doc.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File path is invalid: " + doc.getFileName());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDocumentResponse> getDocumentsByPatientId(Long patientId) {
        return patientDocumentRepository.findByPatientIdOrderByUploadedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDocumentResponse> getDocumentsByClinicalRecordId(Long clinicalRecordId) {
        return patientDocumentRepository.findByClinicalRecordId(clinicalRecordId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDocument(Long id, String performedBy) {
        PatientDocument doc = patientDocumentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with ID: " + id));

        try {
            Path filePath = this.uploadDir.resolve(doc.getStoredFileName()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            logger.warn("Could not delete file from disk: {}", e.getMessage());
        }

        patientDocumentRepository.delete(doc);
        auditService.logAction(performedBy != null ? performedBy : "ADMIN", "DELETE", "PATIENT_DOCUMENT", id.toString(),
                "Deleted document '" + doc.getTitle() + "'");
    }

    private PatientDocumentResponse mapToResponse(PatientDocument d) {
        PatientDocumentResponse resp = new PatientDocumentResponse();
        resp.setId(d.getId());
        if (d.getPatient() != null) {
            resp.setPatientId(d.getPatient().getId());
            resp.setPatientNumber(d.getPatient().getPatientNumber());
            resp.setPatientName(d.getPatient().getFullName());
        }
        if (d.getClinicalRecord() != null) {
            resp.setClinicalRecordId(d.getClinicalRecord().getId());
        }
        resp.setTitle(d.getTitle());
        resp.setDocumentType(d.getDocumentType());
        resp.setFileName(d.getFileName());
        resp.setFileType(d.getFileType());
        resp.setFileSize(d.getFileSize());
        resp.setDownloadUrl("/api/documents/" + d.getId() + "/download");
        resp.setNotes(d.getNotes());
        resp.setUploadedBy(d.getUploadedBy());
        resp.setUploadedAt(d.getUploadedAt());
        return resp;
    }
}
