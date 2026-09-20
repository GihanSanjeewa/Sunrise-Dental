package com.sunrise.dental.service;

import com.sunrise.dental.dto.response.PatientDocumentResponse;
import com.sunrise.dental.enums.DocumentType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PatientDocumentService {

    PatientDocumentResponse uploadDocument(Long patientId, Long clinicalRecordId, String title,
                                          DocumentType documentType, String notes,
                                          MultipartFile file, String uploadedBy);

    PatientDocumentResponse getDocumentById(Long id);

    Resource loadFileAsResource(Long id);

    List<PatientDocumentResponse> getDocumentsByPatientId(Long patientId);

    List<PatientDocumentResponse> getDocumentsByClinicalRecordId(Long clinicalRecordId);

    void deleteDocument(Long id, String performedBy);
}
