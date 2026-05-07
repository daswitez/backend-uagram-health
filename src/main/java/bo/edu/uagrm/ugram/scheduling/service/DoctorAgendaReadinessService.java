package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAgendaReadinessResponse;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorScheduleSettingsRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorWeeklyAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorAgendaReadinessService {

    static final String PROFILE_INCOMPLETE = "PROFILE_PROFESSIONAL_INCOMPLETE";
    static final String WEEKLY_AVAILABILITY_NOT_CONFIGURED = "WEEKLY_AVAILABILITY_NOT_CONFIGURED";
    static final String SCHEDULE_SETTINGS_NOT_CONFIGURED = "SCHEDULE_SETTINGS_NOT_CONFIGURED";

    private final DoctorRepository doctorRepository;
    private final DoctorWeeklyAvailabilityRepository doctorWeeklyAvailabilityRepository;
    private final DoctorScheduleSettingsRepository doctorScheduleSettingsRepository;

    @Transactional(readOnly = true)
    public DoctorAgendaReadinessResponse getMyAgendaReadiness(UUID userId) {
        Doctor doctor = findDoctorByUserId(userId);
        return evaluateReadiness(doctor);
    }

    @Transactional(readOnly = true)
    public boolean isAgendaReadyForPublishing(UUID doctorUserId) {
        Doctor doctor = findDoctorByUserId(doctorUserId);
        return evaluateReadiness(doctor).isReadyForPublishing();
    }

    private DoctorAgendaReadinessResponse evaluateReadiness(Doctor doctor) {
        User user = doctor.getUser();

        boolean profileComplete = isProfileComplete(user, doctor);
        boolean weeklyAvailabilityConfigured =
                !doctorWeeklyAvailabilityRepository.findByDoctorId(user.getId()).isEmpty();
        boolean scheduleSettingsConfigured =
                doctorScheduleSettingsRepository.findByDoctorId(user.getId()).isPresent();

        List<String> missingRequirements = new ArrayList<>();
        if (!profileComplete) {
            missingRequirements.add(PROFILE_INCOMPLETE);
        }
        if (!weeklyAvailabilityConfigured) {
            missingRequirements.add(WEEKLY_AVAILABILITY_NOT_CONFIGURED);
        }
        if (!scheduleSettingsConfigured) {
            missingRequirements.add(SCHEDULE_SETTINGS_NOT_CONFIGURED);
        }

        return DoctorAgendaReadinessResponse.builder()
                .doctorUserId(user.getId())
                .profileComplete(profileComplete)
                .weeklyAvailabilityConfigured(weeklyAvailabilityConfigured)
                .scheduleSettingsConfigured(scheduleSettingsConfigured)
                .readyForPublishing(missingRequirements.isEmpty())
                .missingRequirements(missingRequirements)
                .build();
    }

    private Doctor findDoctorByUserId(UUID userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
    }

    private boolean isProfileComplete(User user, Doctor doctor) {
        return hasText(user.getFirstName())
                && hasText(user.getLastName())
                && hasText(doctor.getSpecialty())
                && hasText(doctor.getMedicalLicense());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
