package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.common.exception.BusinessException;
import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityBlockRequest;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityBlockResponse;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorAvailabilityBlock;
import bo.edu.uagrm.ugram.scheduling.repository.AppointmentRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorAvailabilityBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityBlockService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository;

    @Transactional(readOnly = true)
    public List<DoctorAvailabilityBlockResponse> getMyBlocks(UUID userId, LocalDate dateFrom, LocalDate dateTo) {
        findDoctorByUserId(userId);

        List<DoctorAvailabilityBlock> blocks;
        if (dateFrom != null && dateTo != null) {
            if (dateTo.isBefore(dateFrom)) {
                throw new BusinessException("La fecha final no puede ser anterior a la fecha inicial");
            }
            blocks = doctorAvailabilityBlockRepository.findByDoctorIdAndDateRange(
                    userId,
                    dateFrom.atStartOfDay(),
                    dateTo.plusDays(1).atStartOfDay()
            );
        } else if (dateFrom == null && dateTo == null) {
            blocks = doctorAvailabilityBlockRepository.findByDoctorIdOrderByStartAtAsc(userId);
        } else {
            throw new BusinessException("Debes enviar ambas fechas o ninguna para consultar bloqueos");
        }

        return blocks.stream()
                .map(this::mapResponse)
                .toList();
    }

    @Transactional
    public DoctorAvailabilityBlockResponse createMyBlock(
            UUID userId,
            DoctorAvailabilityBlockRequest request) {
        Doctor doctor = findDoctorByUserId(userId);
        ResolvedBlockWindow blockWindow = resolveBlockWindow(request);

        if (doctorAvailabilityBlockRepository.existsOverlap(userId, blockWindow.startAt(), blockWindow.endAt())) {
            throw new BusinessException("Ya existe otro bloqueo del médico que se solapa con ese rango");
        }

        if (appointmentRepository.existsFutureConflict(
                userId,
                blockWindow.startAt(),
                blockWindow.endAt(),
                LocalDateTime.now())) {
            throw new BusinessException("El bloqueo coincide con citas futuras y no puede registrarse");
        }

        DoctorAvailabilityBlock block = DoctorAvailabilityBlock.builder()
                .doctor(doctor.getUser())
                .startAt(blockWindow.startAt())
                .endAt(blockWindow.endAt())
                .allDay(blockWindow.allDay())
                .reason(request.getReason().trim())
                .build();

        return mapResponse(doctorAvailabilityBlockRepository.save(block));
    }

    @Transactional
    public DoctorAvailabilityBlockResponse updateMyBlock(
            UUID userId,
            UUID blockId,
            DoctorAvailabilityBlockRequest request) {
        findDoctorByUserId(userId);
        DoctorAvailabilityBlock block = doctorAvailabilityBlockRepository.findByIdAndDoctorId(blockId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorAvailabilityBlock", "id", blockId));

        ResolvedBlockWindow blockWindow = resolveBlockWindow(request);

        if (doctorAvailabilityBlockRepository.existsOverlapExcludingId(
                userId, blockId, blockWindow.startAt(), blockWindow.endAt())) {
            throw new BusinessException("Ya existe otro bloqueo del médico que se solapa con ese rango");
        }

        if (appointmentRepository.existsFutureConflict(
                userId,
                blockWindow.startAt(),
                blockWindow.endAt(),
                LocalDateTime.now())) {
            throw new BusinessException("El bloqueo coincide con citas futuras y no puede registrarse");
        }

        block.setStartAt(blockWindow.startAt());
        block.setEndAt(blockWindow.endAt());
        block.setAllDay(blockWindow.allDay());
        block.setReason(request.getReason().trim());

        return mapResponse(doctorAvailabilityBlockRepository.save(block));
    }

    @Transactional
    public void deleteMyBlock(UUID userId, UUID blockId) {
        DoctorAvailabilityBlock block = doctorAvailabilityBlockRepository.findByIdAndDoctorId(blockId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorAvailabilityBlock", "id", blockId));

        doctorAvailabilityBlockRepository.delete(block);
    }

    private Doctor findDoctorByUserId(UUID userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
    }

    private ResolvedBlockWindow resolveBlockWindow(DoctorAvailabilityBlockRequest request) {
        if (request.getStartTime() == null && request.getEndTime() == null) {
            LocalDateTime startAt = request.getDate().atStartOfDay();
            return new ResolvedBlockWindow(startAt, startAt.plusDays(1), true);
        }

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new BusinessException("Debes enviar ambas horas o ninguna para registrar el bloqueo");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException("La hora de fin debe ser posterior a la hora de inicio");
        }

        return new ResolvedBlockWindow(
                request.getDate().atTime(request.getStartTime()),
                request.getDate().atTime(request.getEndTime()),
                false
        );
    }

    private DoctorAvailabilityBlockResponse mapResponse(DoctorAvailabilityBlock block) {
        return DoctorAvailabilityBlockResponse.builder()
                .id(block.getId())
                .doctorUserId(block.getDoctor().getId())
                .date(block.getStartAt().toLocalDate())
                .startTime(block.isAllDay() ? null : block.getStartAt().toLocalTime())
                .endTime(block.isAllDay() ? null : block.getEndAt().toLocalTime())
                .allDay(block.isAllDay())
                .reason(block.getReason())
                .build();
    }

    private record ResolvedBlockWindow(
            LocalDateTime startAt,
            LocalDateTime endAt,
            boolean allDay) {
    }
}
