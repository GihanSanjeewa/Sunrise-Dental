package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.dto.response.BillResponse;
import com.sunrise.dental.dto.response.DashboardMetricsResponse;
import com.sunrise.dental.dto.response.ReportSummaryResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.entity.Bill;
import com.sunrise.dental.enums.AppointmentStatus;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.enums.PaymentStatus;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.BillRepository;
import com.sunrise.dental.repository.DentistRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;

    public ReportServiceImpl(AppointmentRepository appointmentRepository,
                             BillRepository billRepository,
                             PatientRepository patientRepository,
                             DentistRepository dentistRepository) {
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
    }

    @Override
    public DashboardMetricsResponse getDashboardMetrics() {
        LocalDate today = LocalDate.now();

        long todayApts = appointmentRepository.countByAppointmentDate(today);
        long pendingApts = appointmentRepository.countByStatus(AppointmentStatus.BOOKED);
        long completedApts = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
        long cancelledApts = appointmentRepository.countByStatus(AppointmentStatus.CANCELLED);
        long totalPatients = patientRepository.count();
        long availableDentists = dentistRepository.findByAvailabilityStatus(DentistStatus.AVAILABLE).size();

        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        BigDecimal todayRevenue = billRepository.calculateRevenueBetweenDates(startOfToday, endOfToday);
        BigDecimal pendingRevenue = billRepository.calculatePendingRevenue();

        return new DashboardMetricsResponse(
                todayApts,
                pendingApts,
                completedApts,
                cancelledApts,
                totalPatients,
                availableDentists,
                todayRevenue != null ? todayRevenue : BigDecimal.ZERO,
                pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO
        );
    }

    @Override
    public ReportSummaryResponse getDailyAppointmentReport(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<Appointment> apts = appointmentRepository.findByAppointmentDate(targetDate);

        List<AppointmentResponse> dtoList = apts.stream().map(this::mapToAppointmentResponse).collect(Collectors.toList());

        ReportSummaryResponse report = new ReportSummaryResponse(
                "Daily Appointment Schedule Report",
                targetDate.toString(),
                dtoList.size(),
                BigDecimal.ZERO
        );
        report.setAppointments(dtoList);
        return report;
    }

    @Override
    public ReportSummaryResponse getMonthlyAppointmentReport(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Appointment> allApts = appointmentRepository.findAll().stream()
                .filter(a -> !a.getAppointmentDate().isBefore(start) && !a.getAppointmentDate().isAfter(end))
                .collect(Collectors.toList());

        List<AppointmentResponse> dtoList = allApts.stream().map(this::mapToAppointmentResponse).collect(Collectors.toList());

        ReportSummaryResponse report = new ReportSummaryResponse(
                "Monthly Appointment Trend Report",
                ym.toString(),
                dtoList.size(),
                BigDecimal.ZERO
        );
        report.setAppointments(dtoList);
        return report;
    }

    @Override
    public ReportSummaryResponse getDentistAppointmentReport(Long dentistId, LocalDate fromDate, LocalDate toDate) {
        List<Appointment> apts = appointmentRepository.findByDentistId(dentistId);

        if (fromDate != null) {
            apts = apts.stream().filter(a -> !a.getAppointmentDate().isBefore(fromDate)).collect(Collectors.toList());
        }
        if (toDate != null) {
            apts = apts.stream().filter(a -> !a.getAppointmentDate().isAfter(toDate)).collect(Collectors.toList());
        }

        List<AppointmentResponse> dtoList = apts.stream().map(this::mapToAppointmentResponse).collect(Collectors.toList());

        String period = (fromDate != null ? fromDate.toString() : "All") + " to " + (toDate != null ? toDate.toString() : "Present");
        ReportSummaryResponse report = new ReportSummaryResponse(
                "Dentist Roster & Utilization Report",
                period,
                dtoList.size(),
                BigDecimal.ZERO
        );
        report.setAppointments(dtoList);
        return report;
    }

    @Override
    public ReportSummaryResponse getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        LocalDate start = fromDate != null ? fromDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = toDate != null ? toDate : LocalDate.now();

        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(LocalTime.MAX);

        List<Bill> bills = billRepository.findBillsBetweenDates(startDt, endDt);
        BigDecimal totalRev = billRepository.calculateRevenueBetweenDates(startDt, endDt);

        List<BillResponse> billResponses = bills.stream().map(this::mapToBillResponse).collect(Collectors.toList());

        ReportSummaryResponse report = new ReportSummaryResponse(
                "Clinical Financial Revenue Report",
                start + " to " + end,
                billResponses.size(),
                totalRev != null ? totalRev : BigDecimal.ZERO
        );
        report.setBills(billResponses);
        return report;
    }

    @Override
    public ReportSummaryResponse getPendingPaymentReport() {
        List<Bill> pendingBills = billRepository.findByPaymentStatus(PaymentStatus.PENDING);
        BigDecimal totalPending = billRepository.calculatePendingRevenue();

        List<BillResponse> billResponses = pendingBills.stream().map(this::mapToBillResponse).collect(Collectors.toList());

        ReportSummaryResponse report = new ReportSummaryResponse(
                "Outstanding Receivables & Pending Payments Report",
                "All Pending",
                billResponses.size(),
                totalPending != null ? totalPending : BigDecimal.ZERO
        );
        report.setBills(billResponses);
        return report;
    }

    @Override
    public ReportSummaryResponse getCancelledAppointmentReport(LocalDate fromDate, LocalDate toDate) {
        List<Appointment> cancelledApts = appointmentRepository.findByStatus(AppointmentStatus.CANCELLED);

        if (fromDate != null) {
            cancelledApts = cancelledApts.stream().filter(a -> !a.getAppointmentDate().isBefore(fromDate)).collect(Collectors.toList());
        }
        if (toDate != null) {
            cancelledApts = cancelledApts.stream().filter(a -> !a.getAppointmentDate().isAfter(toDate)).collect(Collectors.toList());
        }

        List<AppointmentResponse> dtoList = cancelledApts.stream().map(this::mapToAppointmentResponse).collect(Collectors.toList());

        String period = (fromDate != null ? fromDate.toString() : "All") + " to " + (toDate != null ? toDate.toString() : "Present");
        ReportSummaryResponse report = new ReportSummaryResponse(
                "Cancelled Appointments Audit Report",
                period,
                dtoList.size(),
                BigDecimal.ZERO
        );
        report.setAppointments(dtoList);
        return report;
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment a) {
        AppointmentResponse resp = new AppointmentResponse();
        resp.setId(a.getId());
        resp.setAppointmentNumber(a.getAppointmentNumber());
        if (a.getPatient() != null) {
            resp.setPatientId(a.getPatient().getId());
            resp.setPatientNumber(a.getPatient().getPatientNumber());
            resp.setPatientName(a.getPatient().getFullName());
            resp.setPatientContactNumber(a.getPatient().getContactNumber());
        }
        if (a.getDentist() != null) {
            resp.setDentistId(a.getDentist().getId());
            resp.setDentistNumber(a.getDentist().getDentistNumber());
            resp.setDentistName(a.getDentist().getName());
            resp.setDentistSpecialization(a.getDentist().getSpecialization());
        }
        if (a.getTreatment() != null) {
            resp.setTreatmentId(a.getTreatment().getId());
            resp.setTreatmentName(a.getTreatment().getTreatmentName());
            resp.setTreatmentCost(a.getTreatment().getTreatmentCost());
            resp.setConsultationFee(a.getTreatment().getConsultationFee());
        }
        resp.setAppointmentDate(a.getAppointmentDate());
        resp.setAppointmentTime(a.getAppointmentTime());
        resp.setStatus(a.getStatus());
        resp.setNotes(a.getNotes());
        resp.setCreatedAt(a.getCreatedAt());
        return resp;
    }

    private BillResponse mapToBillResponse(Bill b) {
        BillResponse resp = new BillResponse();
        resp.setId(b.getId());
        resp.setBillNumber(b.getBillNumber());
        if (b.getAppointment() != null) {
            resp.setAppointmentId(b.getAppointment().getId());
            resp.setAppointmentNumber(b.getAppointment().getAppointmentNumber());
            if (b.getAppointment().getPatient() != null) {
                resp.setPatientName(b.getAppointment().getPatient().getFullName());
                resp.setPatientNumber(b.getAppointment().getPatient().getPatientNumber());
            }
        }
        resp.setConsultationFee(b.getConsultationFee());
        resp.setTreatmentCost(b.getTreatmentCost());
        resp.setSubtotal(b.getSubtotal());
        resp.setDiscountAmount(b.getDiscountAmount());
        resp.setTaxAmount(b.getTaxAmount());
        resp.setTotalAmount(b.getTotalAmount());
        resp.setPaymentStatus(b.getPaymentStatus());
        resp.setCreatedAt(b.getCreatedAt());
        return resp;
    }
}
