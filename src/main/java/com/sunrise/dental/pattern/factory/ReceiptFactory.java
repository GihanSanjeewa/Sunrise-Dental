package com.sunrise.dental.pattern.factory;

import com.sunrise.dental.dto.response.ReceiptResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.entity.Bill;
import com.sunrise.dental.entity.Payment;

/**
 * Factory pattern implementation for assembling formatted, printable official receipts.
 */
public class ReceiptFactory {

    public static ReceiptResponse createReceipt(Bill bill, Payment payment) {
        ReceiptResponse receipt = new ReceiptResponse();
        receipt.setClinicName("SUNRISE DENTAL CLINIC");
        receipt.setClinicLocation("Colombo");

        if (payment != null) {
            receipt.setReceiptNumber(payment.getPaymentNumber());
            receipt.setPaymentMethod(payment.getPaymentMethod().name());
            receipt.setDate(payment.getPaymentDate());
        }

        if (bill != null) {
            receipt.setBillNumber(bill.getBillNumber());
            receipt.setConsultationFee(bill.getConsultationFee());
            receipt.setTreatmentCost(bill.getTreatmentCost());
            receipt.setSubtotal(bill.getSubtotal());
            receipt.setDiscountAmount(bill.getDiscountAmount());
            receipt.setTaxAmount(bill.getTaxAmount());
            receipt.setTotalAmount(bill.getTotalAmount());
            receipt.setPaymentStatus(bill.getPaymentStatus().name());

            Appointment appointment = bill.getAppointment();
            if (appointment != null) {
                receipt.setAppointmentNumber(appointment.getAppointmentNumber());

                if (appointment.getPatient() != null) {
                    receipt.setPatientNumber(appointment.getPatient().getPatientNumber());
                    receipt.setPatientName(appointment.getPatient().getFullName());
                }

                if (appointment.getDentist() != null) {
                    receipt.setDentistName(appointment.getDentist().getName());
                    receipt.setDentistSpecialization(appointment.getDentist().getSpecialization());
                }

                if (appointment.getTreatment() != null) {
                    receipt.setTreatmentName(appointment.getTreatment().getTreatmentName());
                }
            }
        }

        receipt.setFooter("Thank you for choosing Sunrise Dental Clinic.");
        return receipt;
    }
}
