package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Dentist;
import com.sunrise.dental.enums.DentistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {
    Optional<Dentist> findByDentistNumber(String dentistNumber);
    boolean existsByDentistNumber(String dentistNumber);
    List<Dentist> findByAvailabilityStatus(DentistStatus status);

    @Query("SELECT d FROM Dentist d WHERE " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.specialization) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(d.dentistNumber) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Dentist> searchDentists(@Param("query") String query);

    @Query("SELECT MAX(d.id) FROM Dentist d")
    Long findMaxId();
}
