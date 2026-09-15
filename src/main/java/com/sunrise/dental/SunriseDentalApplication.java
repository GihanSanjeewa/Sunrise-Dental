package com.sunrise.dental;

import com.sunrise.dental.entity.Dentist;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.entity.Treatment;
import com.sunrise.dental.entity.User;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.enums.Role;
import com.sunrise.dental.repository.DentistRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.repository.TreatmentRepository;
import com.sunrise.dental.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class SunriseDentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SunriseDentalApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                         PatientRepository patientRepository,
                                         DentistRepository dentistRepository,
                                         TreatmentRepository treatmentRepository,
                                         PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Default Users if not present
            if (userRepository.count() == 0) {
                userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "System Administrator", "admin@sunrisedental.lk", Role.ADMIN));
                userRepository.save(new User("receptionist", passwordEncoder.encode("recept123"), "Kamani Jayawardena", "kamani@sunrisedental.lk", Role.RECEPTIONIST));
                userRepository.save(new User("dr.perera", passwordEncoder.encode("dentist123"), "Dr. Rohan Perera", "rohan.perera@sunrisedental.lk", Role.DENTIST));
                userRepository.save(new User("dr.silva", passwordEncoder.encode("dentist123"), "Dr. Anoma Silva", "anoma.silva@sunrisedental.lk", Role.DENTIST));
            }

            // Seed Default Patients if not present
            if (patientRepository.count() == 0) {
                patientRepository.save(new Patient("P-000001", "Sunil Wickramasinghe", "No. 45, Galle Road, Colombo 03", "0771234567", "sunil.w@gmail.com", LocalDate.of(1985, 4, 12), "MALE"));
                patientRepository.save(new Patient("P-000002", "Nadeeka Bandara", "12/A, Kandy Road, Kelaniya", "0719876543", "nadeeka.b@yahoo.com", LocalDate.of(1992, 8, 23), "FEMALE"));
                patientRepository.save(new Patient("P-000003", "Dinesh Cooray", "78, Duplication Road, Colombo 04", "0765551234", "dinesh.cooray@outlook.com", LocalDate.of(1978, 11, 30), "MALE"));
            }

            // Seed Default Dentists if not present
            if (dentistRepository.count() == 0) {
                dentistRepository.save(new Dentist("D-000001", "Dr. Rohan Perera", "Orthodontics & Dentofacial Orthopedics", "0772221100", "rohan.perera@sunrisedental.lk", DentistStatus.AVAILABLE));
                dentistRepository.save(new Dentist("D-000002", "Dr. Anoma Silva", "Endodontics & Conservative Dentistry", "0713332211", "anoma.silva@sunrisedental.lk", DentistStatus.AVAILABLE));
                dentistRepository.save(new Dentist("D-000003", "Dr. Kasun Fernando", "General Dentistry & Prosthetics", "0764443322", "kasun.fernando@sunrisedental.lk", DentistStatus.AVAILABLE));
            }

            // Seed Default Treatments with Dynamic Database Pricing if not present
            if (treatmentRepository.count() == 0) {
                treatmentRepository.save(new Treatment("TRT-001", "Dental Consultation", "Comprehensive clinical oral examination and diagnosis.", BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00), "ACTIVE"));
                treatmentRepository.save(new Treatment("TRT-002", "Teeth Cleaning (Scaling)", "Full mouth ultrasonic calculus debridement and polish.", BigDecimal.valueOf(4500.00), BigDecimal.valueOf(1500.00), "ACTIVE"));
                treatmentRepository.save(new Treatment("TRT-003", "Dental Composite Filling", "Tooth-colored aesthetic restoration per tooth.", BigDecimal.valueOf(3500.00), BigDecimal.valueOf(1500.00), "ACTIVE"));
                treatmentRepository.save(new Treatment("TRT-004", "Tooth Extraction", "Simple or surgical tooth extraction under local anesthesia.", BigDecimal.valueOf(6000.00), BigDecimal.valueOf(2000.00), "ACTIVE"));
                treatmentRepository.save(new Treatment("TRT-005", "Root Canal Treatment", "Endodontic therapy, cleaning, shaping and obturation.", BigDecimal.valueOf(18000.00), BigDecimal.valueOf(2500.00), "ACTIVE"));
            }
        };
    }
}
