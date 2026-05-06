package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.common.exception.BusinessException;
import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityResponse;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityUpdateRequest;
import bo.edu.uagrm.ugram.scheduling.dto.WeeklyAvailabilitySlotRequest;
import bo.edu.uagrm.ugram.scheduling.dto.WeeklyAvailabilitySlotResponse;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorWeeklyAvailability;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorWeeklyAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorWeeklyAvailabilityRepository doctorWeeklyAvailabilityRepository;

    @Transactional(readOnly = true)
    public DoctorAvailabilityResponse getMyAvailability(UUID userId) {
        Doctor doctor = findDoctorByUserId(userId);
        List<DoctorWeeklyAvailability> availability = doctorWeeklyAvailabilityRepository.findByDoctorId(userId);
        return mapResponse(doctor.getUser(), availability);
    }

    @Transactional
    public DoctorAvailabilityResponse updateMyAvailability(
            UUID userId,
            DoctorAvailabilityUpdateRequest request) {
        Doctor doctor = findDoctorByUserId(userId);
        validateWeeklyAvailability(request.getWeeklyAvailability());

        doctorWeeklyAvailabilityRepository.deleteByDoctorId(userId);

        List<DoctorWeeklyAvailability> entities = request.getWeeklyAvailability().stream()
                .map(slot -> DoctorWeeklyAvailability.builder()
                        .doctor(doctor.getUser())
                        .dayOfWeek(slot.getDayOfWeek())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .build())
                .toList();

        List<DoctorWeeklyAvailability> saved = doctorWeeklyAvailabilityRepository.saveAll(entities);
        return mapResponse(doctor.getUser(), saved);
    }

    private Doctor findDoctorByUserId(UUID userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
    }

    private void validateWeeklyAvailability(List<WeeklyAvailabilitySlotRequest> weeklyAvailability) {
        List<WeeklyAvailabilitySlotRequest> sortedSlots = new ArrayList<>(weeklyAvailability);
        sortedSlots.sort(Comparator
                .comparing(WeeklyAvailabilitySlotRequest::getDayOfWeek)
                .thenComparing(WeeklyAvailabilitySlotRequest::getStartTime)
                .thenComparing(WeeklyAvailabilitySlotRequest::getEndTime));

        WeeklyAvailabilitySlotRequest previous = null;
        for (WeeklyAvailabilitySlotRequest current : sortedSlots) {
            if (!current.getEndTime().isAfter(current.getStartTime())) {
                throw new BusinessException("La franja horaria debe tener una hora de fin posterior a la de inicio");
            }

            if (previous != null
                    && previous.getDayOfWeek() == current.getDayOfWeek()
                    && current.getStartTime().isBefore(previous.getEndTime())) {
                throw new BusinessException("No se permiten franjas semanales solapadas para el mismo día");
            }

            previous = current;
        }
    }

    private DoctorAvailabilityResponse mapResponse(User doctor, List<DoctorWeeklyAvailability> availability) {
        List<WeeklyAvailabilitySlotResponse> weeklyAvailability = availability.stream()
                .sorted(Comparator
                        .comparing((DoctorWeeklyAvailability slot) -> slot.getDayOfWeek().getValue())
                        .thenComparing(DoctorWeeklyAvailability::getStartTime)
                        .thenComparing(DoctorWeeklyAvailability::getEndTime))
                .map(slot -> WeeklyAvailabilitySlotResponse.builder()
                        .id(slot.getId())
                        .dayOfWeek(slot.getDayOfWeek())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .build())
                .toList();

        return DoctorAvailabilityResponse.builder()
                .doctorUserId(doctor.getId())
                .weeklyAvailability(weeklyAvailability)
                .build();
    }
}
