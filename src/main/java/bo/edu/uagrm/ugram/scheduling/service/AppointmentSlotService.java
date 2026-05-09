package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.AppointmentSlotResponse;
import bo.edu.uagrm.ugram.scheduling.dto.AppointmentSlotsResponse;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorScheduleSettings;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorWeeklyAvailability;
import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHoliday;
import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHolidayType;
import bo.edu.uagrm.ugram.scheduling.repository.AppointmentRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorAvailabilityBlockRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorScheduleSettingsRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorWeeklyAvailabilityRepository;
import bo.edu.uagrm.ugram.scheduling.repository.InstitutionalHolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentSlotService {

    private final DoctorRepository doctorRepository;
    private final DoctorAgendaReadinessService doctorAgendaReadinessService;
    private final DoctorWeeklyAvailabilityRepository doctorWeeklyAvailabilityRepository;
    private final DoctorScheduleSettingsRepository doctorScheduleSettingsRepository;
    private final InstitutionalHolidayRepository institutionalHolidayRepository;
    private final DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public AppointmentSlotsResponse getAvailableSlots(UUID doctorId, LocalDate date) {
        Doctor doctor = findDoctorByUserId(doctorId);
        boolean readyForPublishing = doctorAgendaReadinessService.isAgendaReadyForPublishing(doctor.getUser().getId());

        if (!readyForPublishing) {
            return buildResponse(doctor.getUser().getId(), date, null, false, List.of());
        }

        DoctorScheduleSettings settings = doctorScheduleSettingsRepository.findByDoctorId(doctor.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("DoctorScheduleSettings", "doctorId", doctor.getUser().getId()));
        int appointmentDurationMinutes = settings.getAppointmentDurationMinutes();

        List<DoctorWeeklyAvailability> availabilityRanges =
                doctorWeeklyAvailabilityRepository.findByDoctorIdAndDayOfWeekOrderByStartTimeAsc(
                        doctor.getUser().getId(), date.getDayOfWeek());
        if (availabilityRanges.isEmpty()) {
            return buildResponse(doctor.getUser().getId(), date, appointmentDurationMinutes, true, List.of());
        }

        List<InstitutionalHoliday> holidays = institutionalHolidayRepository.findByDate(date);
        if (holidays.stream().anyMatch(holiday -> holiday.getType() == InstitutionalHolidayType.TOTAL)) {
            return buildResponse(doctor.getUser().getId(), date, appointmentDurationMinutes, true, List.of());
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        List<TimeRange> restrictedRanges = collectRestrictedRanges(
                date,
                doctor.getUser().getId(),
                dayStart,
                dayEnd,
                holidays
        );

        List<AppointmentSlotResponse> slots = availabilityRanges.stream()
                .flatMap(availability -> generateSlotsForRange(availability, date, appointmentDurationMinutes, restrictedRanges).stream())
                .sorted(Comparator.comparing(AppointmentSlotResponse::getStartAt))
                .toList();

        return buildResponse(doctor.getUser().getId(), date, appointmentDurationMinutes, true, slots);
    }

    private List<TimeRange> collectRestrictedRanges(
            LocalDate date,
            UUID doctorId,
            LocalDateTime dayStart,
            LocalDateTime dayEnd,
            List<InstitutionalHoliday> holidays) {
        List<TimeRange> restrictedRanges = new ArrayList<>();

        holidays.stream()
                .filter(holiday -> holiday.getType() == InstitutionalHolidayType.PARTIAL)
                .map(holiday -> new TimeRange(date.atTime(holiday.getStartTime()), date.atTime(holiday.getEndTime())))
                .forEach(restrictedRanges::add);

        doctorAvailabilityBlockRepository.findByDoctorIdAndDateRange(doctorId, dayStart, dayEnd).stream()
                .map(block -> new TimeRange(block.getStartAt(), block.getEndAt()))
                .forEach(restrictedRanges::add);

        appointmentRepository.findActiveByDoctorIdAndRange(doctorId, dayStart, dayEnd).stream()
                .map(appointment -> new TimeRange(appointment.getScheduledStart(), appointment.getScheduledEnd()))
                .forEach(restrictedRanges::add);

        return restrictedRanges;
    }

    private List<AppointmentSlotResponse> generateSlotsForRange(
            DoctorWeeklyAvailability availability,
            LocalDate date,
            int appointmentDurationMinutes,
            List<TimeRange> restrictedRanges) {
        List<AppointmentSlotResponse> slots = new ArrayList<>();
        LocalDateTime cursor = date.atTime(availability.getStartTime());
        LocalDateTime availabilityEnd = date.atTime(availability.getEndTime());

        while (!cursor.plusMinutes(appointmentDurationMinutes).isAfter(availabilityEnd)) {
            LocalDateTime slotEnd = cursor.plusMinutes(appointmentDurationMinutes);
            TimeRange candidate = new TimeRange(cursor, slotEnd);

            if (restrictedRanges.stream().noneMatch(restriction -> restriction.overlaps(candidate))) {
                slots.add(AppointmentSlotResponse.builder()
                        .startAt(cursor)
                        .endAt(slotEnd)
                        .build());
            }

            cursor = slotEnd;
        }

        return slots;
    }

    private AppointmentSlotsResponse buildResponse(
            UUID doctorId,
            LocalDate date,
            Integer appointmentDurationMinutes,
            boolean readyForPublishing,
            List<AppointmentSlotResponse> slots) {
        return AppointmentSlotsResponse.builder()
                .doctorId(doctorId)
                .date(date)
                .appointmentDurationMinutes(appointmentDurationMinutes)
                .readyForPublishing(readyForPublishing)
                .slots(slots)
                .build();
    }

    private Doctor findDoctorByUserId(UUID doctorId) {
        return doctorRepository.findByUserId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", doctorId));
    }

    private record TimeRange(LocalDateTime start, LocalDateTime end) {
        private boolean overlaps(TimeRange other) {
            return start.isBefore(other.end()) && end.isAfter(other.start());
        }
    }
}
