package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorScheduleSettingsRequest;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorScheduleSettingsResponse;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorScheduleSettings;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorScheduleSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorScheduleSettingsService {

    private static final int DEFAULT_APPOINTMENT_DURATION_MINUTES = 20;

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleSettingsRepository doctorScheduleSettingsRepository;

    @Transactional(readOnly = true)
    public DoctorScheduleSettingsResponse getMyScheduleSettings(UUID userId) {
        Doctor doctor = findDoctorByUserId(userId);

        return doctorScheduleSettingsRepository.findByDoctorId(userId)
                .map(settings -> mapResponse(doctor.getUser().getId(), settings.getAppointmentDurationMinutes(), true))
                .orElseGet(() -> mapResponse(doctor.getUser().getId(), DEFAULT_APPOINTMENT_DURATION_MINUTES, false));
    }

    @Transactional
    public DoctorScheduleSettingsResponse updateMyScheduleSettings(
            UUID userId,
            DoctorScheduleSettingsRequest request) {
        Doctor doctor = findDoctorByUserId(userId);

        DoctorScheduleSettings settings = doctorScheduleSettingsRepository.findByDoctorId(userId)
                .orElseGet(() -> DoctorScheduleSettings.builder()
                        .doctor(doctor.getUser())
                        .build());

        settings.setAppointmentDurationMinutes(request.getAppointmentDurationMinutes());

        DoctorScheduleSettings saved = doctorScheduleSettingsRepository.save(settings);
        return mapResponse(doctor.getUser().getId(), saved.getAppointmentDurationMinutes(), true);
    }

    private Doctor findDoctorByUserId(UUID userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
    }

    private DoctorScheduleSettingsResponse mapResponse(
            UUID doctorUserId,
            Integer appointmentDurationMinutes,
            boolean configured) {
        return DoctorScheduleSettingsResponse.builder()
                .doctorUserId(doctorUserId)
                .appointmentDurationMinutes(appointmentDurationMinutes)
                .configured(configured)
                .build();
    }
}
