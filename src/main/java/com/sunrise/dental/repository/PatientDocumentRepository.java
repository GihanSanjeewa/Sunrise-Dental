package com.sunrise.dental.repository;

import com.sunrise.dental.entity.PatientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientDocumentRepository extends JpaRepository<PatientDocument, Long> {

    List<PatientDocument> findByPatientIdOrderByUploadedAtDesc(Long patientId);

    List<PatientDocument> findByClinicalRecordId(Long clinicalRecordId);
}
