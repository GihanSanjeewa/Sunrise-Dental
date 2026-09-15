package com.sunrise.dental.util;

import java.time.LocalDate;

public class IdSequenceGenerator {

    public static String generatePatientNumber(Long nextId) {
        long id = nextId != null ? nextId : 1L;
        return String.format("P-%06d", id);
    }

    public static String generateDentistNumber(Long nextId) {
        long id = nextId != null ? nextId : 1L;
        return String.format("D-%06d", id);
    }

    public static String generateTreatmentCode(Long nextId) {
        long id = nextId != null ? nextId : 1L;
        return String.format("TRT-%03d", id);
    }

    public static String generateAppointmentNumber(Long nextId) {
        int year = LocalDate.now().getYear();
        long id = nextId != null ? nextId : 1L;
        return String.format("APT-%d-%06d", year, id);
    }

    public static String generateBillNumber(Long nextId) {
        int year = LocalDate.now().getYear();
        long id = nextId != null ? nextId : 1L;
        return String.format("BILL-%d-%06d", year, id);
    }

    public static String generatePaymentNumber(Long nextId) {
        int year = LocalDate.now().getYear();
        long id = nextId != null ? nextId : 1L;
        return String.format("PAY-%d-%06d", year, id);
    }
}
