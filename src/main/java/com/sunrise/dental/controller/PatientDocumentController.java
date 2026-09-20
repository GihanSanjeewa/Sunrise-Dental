package com.sunrise.dental.controller;

import com.sunrise.dental.dto.response.PatientDocumentResponse;
import com.sunrise.dental.enums.DocumentType;
import com.sunrise.dental.service.PatientDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Patient Documents", description = "Secure medical document upload, file storage, and retrieval")
public class PatientDocumentController {

    private final PatientDocumentService patientDocumentService;

    public PatientDocumentController(PatientDocumentService patientDocumentService) {
        this.patientDocumentService = patientDocumentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload patient document, X-Ray, consent form, or lab report")
    public ResponseEntity<PatientDocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("patientId") Long patientId,
            @RequestParam(value = "clinicalRecordId", required = false) Long clinicalRecordId,
            @RequestParam("title") String title,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "notes", required = false) String notes,
            Authentication authentication) {
        String uploadedBy = authentication != null ? authentication.getName() : "SYSTEM";
        PatientDocumentResponse response = patientDocumentService.uploadDocument(
                patientId, clinicalRecordId, title, documentType, notes, file, uploadedBy);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document metadata by ID")
    public ResponseEntity<PatientDocumentResponse> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(patientDocumentService.getDocumentById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all documents belonging to a patient")
    public ResponseEntity<List<PatientDocumentResponse>> getDocumentsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientDocumentService.getDocumentsByPatientId(patientId));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download or stream document binary file")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        PatientDocumentResponse doc = patientDocumentService.getDocumentById(id);
        Resource resource = patientDocumentService.loadFileAsResource(id);

        String contentType = doc.getFileType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete patient document")
    public ResponseEntity<Map<String, String>> deleteDocument(@PathVariable Long id, Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "SYSTEM";
        patientDocumentService.deleteDocument(id, performedBy);
        return ResponseEntity.ok(Map.of("message", "Document deleted successfully."));
    }
}
