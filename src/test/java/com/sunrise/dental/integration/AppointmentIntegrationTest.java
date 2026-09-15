package com.sunrise.dental.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunrise.dental.dto.request.AppointmentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AppointmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("IT001: End-to-End Appointment Booking and Double-Booking Conflict Prevention")
    void testAppointmentBookingLifecycleAndConflictDetection() throws Exception {
        LocalDate appointmentDate = LocalDate.now().plusDays(3);
        LocalTime appointmentTime = LocalTime.of(10, 0);

        AppointmentRequest request = new AppointmentRequest(
                1L, // Patient Sunil Wickramasinghe
                1L, // Dentist Dr. Rohan Perera
                1L, // Treatment Dental Consultation
                appointmentDate,
                appointmentTime,
                "E2E Integration Test Booking"
        );

        // 1. First booking should succeed (201 Created)
        String responseContent = mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentNumber", notNullValue()))
                .andExpect(jsonPath("$.status", is("BOOKED")))
                .andExpect(jsonPath("$.patientName", is("Sunil Wickramasinghe")))
                .andExpect(jsonPath("$.dentistName", is("Dr. Rohan Perera")))
                .andReturn().getResponse().getContentAsString();

        String generatedAptNum = objectMapper.readTree(responseContent).get("appointmentNumber").asText();

        // 2. Second booking for SAME dentist at SAME date and time must be REJECTED (409 Conflict)
        AppointmentRequest conflictingRequest = new AppointmentRequest(
                2L, // Patient Nadeeka Bandara
                1L, // SAME Dentist Dr. Rohan Perera
                2L, // Treatment Teeth Cleaning
                appointmentDate,
                appointmentTime,
                "Attempting conflicting double-booking slot"
        );

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conflictingRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Selected dentist is already booked for this time.")));

        // 3. Search created appointment by appointment number (200 OK)
        mockMvc.perform(get("/api/appointments/number/" + generatedAptNum))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentNumber", is(generatedAptNum)))
                .andExpect(jsonPath("$.patientName", is("Sunil Wickramasinghe")));

        // 4. Search non-existent appointment number (404 Not Found)
        mockMvc.perform(get("/api/appointments/number/APT-9999-999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No appointment found with the provided appointment number.")));
    }
}
