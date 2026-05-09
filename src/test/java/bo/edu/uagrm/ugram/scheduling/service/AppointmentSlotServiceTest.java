package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.entity.UserType;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.AppointmentSlotsResponse;
import bo.edu.uagrm.ugram.scheduling.entity.Appointment;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorAvailabilityBlock;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorScheduleSettings;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorWeeklyAvailability;
import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHoliday;
import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHolidayType;
import bo.edu.uagrm.ugram.scheduling.repository.AppointmentRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorAvailabilityBlockRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorScheduleSettingsRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorWeeklyAvailabilityRepository;
import bo.edu.uagrm.ugram.scheduling.repository.InstitutionalHolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentSlotServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorAgendaReadinessService doctorAgendaReadinessService;

    @Mock
    private DoctorWeeklyAvailabilityRepository doctorWeeklyAvailabilityRepository;

    @Mock
    private DoctorScheduleSettingsRepository doctorScheduleSettingsRepository;

    @Mock
    private InstitutionalHolidayRepository institutionalHolidayRepository;

    @Mock
    private DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private AppointmentSlotService appointmentSlotService;

    @BeforeEach
    void setUp() {
        appointmentSlotService = new AppointmentSlotService(
                doctorRepository,
                doctorAgendaReadinessService,
                doctorWeeklyAvailabilityRepository,
                doctorScheduleSettingsRepository,
                institutionalHolidayRepository,
                doctorAvailabilityBlockRepository,
                appointmentRepository
        );
    }

    @Test
    void shouldGenerateOrderedSlotsForBusinessDayWithoutRestrictions() {
        UUID doctorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 25);
        Doctor doctor = buildDoctor(doctorId);

        mockReadyDoctor(doctorId, doctor, 20);
        when(doctorWeeklyAvailabilityRepository.findByDoctorIdAndDayOfWeekOrderByStartTimeAsc(doctorId, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability(doctor.getUser(), DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 0))));
        when(institutionalHolidayRepository.findByDate(date)).thenReturn(List.of());
        when(doctorAvailabilityBlockRepository.findByDoctorIdAndDateRange(
                doctorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of());
        when(appointmentRepository.findActiveByDoctorIdAndRange(
                doctorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of());

        AppointmentSlotsResponse response = appointmentSlotService.getAvailableSlots(doctorId, date);

        assertThat(response.isReadyForPublishing()).isTrue();
        assertThat(response.getAppointmentDurationMinutes()).isEqualTo(20);
        assertThat(response.getSlots()).extracting("startAt").containsExactly(
                LocalDateTime.of(2026, 5, 25, 8, 0),
                LocalDateTime.of(2026, 5, 25, 8, 20),
                LocalDateTime.of(2026, 5, 25, 8, 40)
        );
        assertThat(response.getSlots()).extracting("endAt").containsExactly(
                LocalDateTime.of(2026, 5, 25, 8, 20),
                LocalDateTime.of(2026, 5, 25, 8, 40),
                LocalDateTime.of(2026, 5, 25, 9, 0)
        );
    }

    @Test
    void shouldReturnNoSlotsForTotalHoliday() {
        UUID doctorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 25);
        Doctor doctor = buildDoctor(doctorId);

        mockReadyDoctor(doctorId, doctor, 20);
        when(doctorWeeklyAvailabilityRepository.findByDoctorIdAndDayOfWeekOrderByStartTimeAsc(doctorId, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability(doctor.getUser(), DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0))));
        when(institutionalHolidayRepository.findByDate(date))
                .thenReturn(List.of(InstitutionalHoliday.builder()
                        .date(date)
                        .type(InstitutionalHolidayType.TOTAL)
                        .reason("Feriado institucional")
                        .build()));

        AppointmentSlotsResponse response = appointmentSlotService.getAvailableSlots(doctorId, date);

        assertThat(response.isReadyForPublishing()).isTrue();
        assertThat(response.getSlots()).isEmpty();
    }

    @Test
    void shouldExcludePartialHolidayBlocksAndExistingAppointments() {
        UUID doctorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 25);
        Doctor doctor = buildDoctor(doctorId);

        mockReadyDoctor(doctorId, doctor, 20);
        when(doctorWeeklyAvailabilityRepository.findByDoctorIdAndDayOfWeekOrderByStartTimeAsc(doctorId, DayOfWeek.MONDAY))
                .thenReturn(List.of(availability(doctor.getUser(), DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0))));
        when(institutionalHolidayRepository.findByDate(date))
                .thenReturn(List.of(InstitutionalHoliday.builder()
                        .date(date)
                        .type(InstitutionalHolidayType.PARTIAL)
                        .startTime(LocalTime.of(8, 20))
                        .endTime(LocalTime.of(8, 40))
                        .reason("Jornada parcial")
                        .build()));
        when(doctorAvailabilityBlockRepository.findByDoctorIdAndDateRange(
                doctorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(DoctorAvailabilityBlock.builder()
                        .doctor(doctor.getUser())
                        .startAt(LocalDateTime.of(2026, 5, 25, 9, 0))
                        .endAt(LocalDateTime.of(2026, 5, 25, 10, 0))
                        .reason("Ausencia")
                        .build()));
        when(appointmentRepository.findActiveByDoctorIdAndRange(
                doctorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(Appointment.builder()
                        .doctor(doctor.getUser())
                        .scheduledStart(LocalDateTime.of(2026, 5, 25, 8, 40))
                        .scheduledEnd(LocalDateTime.of(2026, 5, 25, 9, 0))
                        .build()));

        AppointmentSlotsResponse response = appointmentSlotService.getAvailableSlots(doctorId, date);

        assertThat(response.getSlots()).extracting("startAt").containsExactly(
                LocalDateTime.of(2026, 5, 25, 8, 0)
        );
    }

    @Test
    void shouldReturnNoSlotsWhenAgendaIsNotReadyForPublishing() {
        UUID doctorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 5, 25);
        Doctor doctor = buildDoctor(doctorId);

        when(doctorRepository.findByUserId(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorAgendaReadinessService.isAgendaReadyForPublishing(doctorId)).thenReturn(false);

        AppointmentSlotsResponse response = appointmentSlotService.getAvailableSlots(doctorId, date);

        assertThat(response.isReadyForPublishing()).isFalse();
        assertThat(response.getAppointmentDurationMinutes()).isNull();
        assertThat(response.getSlots()).isEmpty();
    }

    private void mockReadyDoctor(UUID doctorId, Doctor doctor, int appointmentDurationMinutes) {
        when(doctorRepository.findByUserId(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorAgendaReadinessService.isAgendaReadyForPublishing(doctorId)).thenReturn(true);
        when(doctorScheduleSettingsRepository.findByDoctorId(doctorId))
                .thenReturn(Optional.of(DoctorScheduleSettings.builder()
                        .doctor(doctor.getUser())
                        .appointmentDurationMinutes(appointmentDurationMinutes)
                        .build()));
    }

    private DoctorWeeklyAvailability availability(User doctorUser, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        return DoctorWeeklyAvailability.builder()
                .doctor(doctorUser)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private Doctor buildDoctor(UUID userId) {
        User user = User.builder()
                .id(userId)
                .email("doctor.test@uagrm.edu.bo")
                .passwordHash("hash")
                .firstName("Doctor")
                .lastName("Test")
                .userType(UserType.DOCTOR)
                .build();

        return Doctor.builder()
                .id(UUID.randomUUID())
                .user(user)
                .specialty("Medicina Familiar")
                .medicalLicense("MP-12345")
                .build();
    }
}
