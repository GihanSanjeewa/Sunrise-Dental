package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);

    boolean existsByAppointmentNumber(String appointmentNumber);

    /**
     * Conflict detection: Returns true if the dentist already has an active (non-cancelled)
     * appointment scheduled for the given date and time.
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE " +
           "a.dentist.id = :dentistId AND " +
           "a.appointmentDate = :date AND " +
           "a.appointmentTime = :time AND " +
           "a.status != 'CANCELLED'")
    boolean existsActiveSlotForDentist(@Param("dentistId") Long dentistId,
                                      @Param("date") LocalDate date,
                                      @Param("time") LocalTime time);

    /**
     * Conflict detection excluding the current appointment (for update operations).
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE " +
           "a.dentist.id = :dentistId AND " +
           "a.appointmentDate = :date AND " +
           "a.appointmentTime = :time AND " +
           "a.status != 'CANCELLED' AND " +
           "a.id != :excludeId")
    boolean existsActiveSlotForDentistExcludingId(@Param("dentistId") Long dentistId,
                                                @Param("date") LocalDate date,
                                                @Param("time") LocalTime time,
                                                @Param("excludeId") Long excludeId);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByDentistId(Long dentistId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:aptNum IS NULL OR LOWER(a.appointmentNumber) LIKE LOWER(CONCAT('%', :aptNum, '%'))) AND " +
           "(:patientNum IS NULL OR LOWER(a.patient.patientNumber) LIKE LOWER(CONCAT('%', :patientNum, '%'))) AND " +
           "(:patientName IS NULL OR LOWER(a.patient.fullName) LIKE LOWER(CONCAT('%', :patientName, '%'))) AND " +
           "(:contactNum IS NULL OR a.patient.contactNumber LIKE CONCAT('%', :contactNum, '%')) AND " +
           "(:dentistId IS NULL OR a.dentist.id = :dentistId) AND " +
           "(:date IS NULL OR a.appointmentDate = :date) AND " +
           "(:status IS NULL OR a.status = :status) " +
           "ORDER BY a.appointmentDate DESC, a.appointmentTime ASC")
    List<Appointment> searchAppointments(
            @Param("aptNum") String aptNum,
            @Param("patientNum") String patientNum,
            @Param("patientName") String patientName,
            @Param("contactNum") String contactNum,
            @Param("dentistId") Long dentistId,
            @Param("date") LocalDate date,
            @Param("status") AppointmentStatus status);

    @Query("SELECT MAX(a.id) FROM Appointment a")
    Long findMaxId();

    long countByAppointmentDate(LocalDate date);

    long countByStatus(AppointmentStatus status);
}
