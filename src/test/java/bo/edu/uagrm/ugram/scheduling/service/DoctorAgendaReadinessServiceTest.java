package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.entity.UserType;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAgendaReadinessResponse;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorScheduleSettings;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorWeeklyAvailability;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorScheduleSettingsRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorWeeklyAvailabilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorAgendaReadinessServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorWeeklyAvailabilityRepository doctorWeeklyAvailabilityRepository;

    @Mock
    private DoctorScheduleSettingsRepository doctorScheduleSettingsRepository;

    private DoctorAgendaReadinessService doctorAgendaReadinessService;

    @BeforeEach
    void setUp() {
        doctorAgendaReadinessService = new DoctorAgendaReadinessService(
                doctorRepository,
                doctorWeeklyAvailabilityRepository,
                doctorScheduleSettingsRepository
        );
    }

    @Test
    void shouldMarkAgendaReadyWhenProfileAvailabilityAndSettingsAreConfigured() {
        UUID userId = UUID.randomUUID();
        Doctor doctor = buildDoctor(userId, "Cardiologia", "MP-1001");

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
        when(doctorWeeklyAvailabilityRepository.findByDoctorId(userId))
                .thenReturn(List.of(DoctorWeeklyAvailability.builder()
                        .doctor(doctor.getUser())
                        .dayOfWeek(DayOfWeek.MONDAY)
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(12, 0))
                        .build()));
        when(doctorScheduleSettingsRepository.findByDoctorId(userId))
                .thenReturn(Optional.of(DoctorScheduleSettings.builder()
                        .doctor(doctor.getUser())
                        .appointmentDurationMinutes(20)
                        .build()));

        DoctorAgendaReadinessResponse response = doctorAgendaReadinessService.getMyAgendaReadiness(userId);

        assertThat(response.isProfileComplete()).isTrue();
        assertThat(response.isWeeklyAvailabilityConfigured()).isTrue();
        assertThat(response.isScheduleSettingsConfigured()).isTrue();
        assertThat(response.isReadyForPublishing()).isTrue();
        assertThat(response.getMissingRequirements()).isEmpty();
    }

    @Test
    void shouldReportMissingRequirementsWhenAgendaIsIncomplete() {
        UUID userId = UUID.randomUUID();
        Doctor doctor = buildDoctor(userId, "   ", "MP-1001");

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
        when(doctorWeeklyAvailabilityRepository.findByDoctorId(userId)).thenReturn(List.of());
        when(doctorScheduleSettingsRepository.findByDoctorId(userId)).thenReturn(Optional.empty());

        DoctorAgendaReadinessResponse response = doctorAgendaReadinessService.getMyAgendaReadiness(userId);

        assertThat(response.isProfileComplete()).isFalse();
        assertThat(response.isWeeklyAvailabilityConfigured()).isFalse();
        assertThat(response.isScheduleSettingsConfigured()).isFalse();
        assertThat(response.isReadyForPublishing()).isFalse();
        assertThat(response.getMissingRequirements()).containsExactly(
                DoctorAgendaReadinessService.PROFILE_INCOMPLETE,
                DoctorAgendaReadinessService.WEEKLY_AVAILABILITY_NOT_CONFIGURED,
                DoctorAgendaReadinessService.SCHEDULE_SETTINGS_NOT_CONFIGURED
        );
    }

    private Doctor buildDoctor(UUID userId, String specialty, String medicalLicense) {
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
                .specialty(specialty)
                .medicalLicense(medicalLicense)
                .build();
    }
}
