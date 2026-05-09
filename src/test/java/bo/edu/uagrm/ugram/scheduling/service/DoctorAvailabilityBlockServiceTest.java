package bo.edu.uagrm.ugram.scheduling.service;

import bo.edu.uagrm.ugram.common.exception.BusinessException;
import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.entity.UserType;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityBlockRequest;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityBlockResponse;
import bo.edu.uagrm.ugram.scheduling.entity.DoctorAvailabilityBlock;
import bo.edu.uagrm.ugram.scheduling.repository.AppointmentRepository;
import bo.edu.uagrm.ugram.scheduling.repository.DoctorAvailabilityBlockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorAvailabilityBlockServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorAvailabilityBlockRepository doctorAvailabilityBlockRepository;

    private DoctorAvailabilityBlockService doctorAvailabilityBlockService;

    @BeforeEach
    void setUp() {
        doctorAvailabilityBlockService = new DoctorAvailabilityBlockService(
                doctorRepository,
                appointmentRepository,
                doctorAvailabilityBlockRepository
        );
    }

    @Test
    void shouldCreatePartialBlockWhenNoConflictExists() {
        UUID userId = UUID.randomUUID();
        Doctor doctor = buildDoctor(userId);
        DoctorAvailabilityBlockRequest request = DoctorAvailabilityBlockRequest.builder()
                .date(LocalDate.of(2026, 5, 29))
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(12, 0))
                .reason("Licencia médica")
                .build();

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
        when(doctorAvailabilityBlockRepository.existsOverlap(
                eq(userId),
                eq(LocalDateTime.of(2026, 5, 29, 10, 0)),
                eq(LocalDateTime.of(2026, 5, 29, 12, 0))
        )).thenReturn(false);
        when(appointmentRepository.existsFutureConflict(eq(userId), any(), any(), any())).thenReturn(false);
        when(doctorAvailabilityBlockRepository.save(any(DoctorAvailabilityBlock.class))).thenAnswer(invocation -> {
            DoctorAvailabilityBlock block = invocation.getArgument(0);
            block.setId(UUID.randomUUID());
            return block;
        });

        DoctorAvailabilityBlockResponse response = doctorAvailabilityBlockService.createMyBlock(userId, request);

        assertThat(response.isAllDay()).isFalse();
        assertThat(response.getDate()).isEqualTo(LocalDate.of(2026, 5, 29));
        assertThat(response.getStartTime()).isEqualTo(java.time.LocalTime.of(10, 0));
        assertThat(response.getEndTime()).isEqualTo(java.time.LocalTime.of(12, 0));
        assertThat(response.getReason()).isEqualTo("Licencia médica");
    }

    @Test
    void shouldListBlocksByRange() {
        UUID userId = UUID.randomUUID();
        Doctor doctor = buildDoctor(userId);
        DoctorAvailabilityBlock block = DoctorAvailabilityBlock.builder()
                .id(UUID.randomUUID())
                .doctor(doctor.getUser())
                .startAt(LocalDateTime.of(2026, 5, 29, 10, 0))
                .endAt(LocalDateTime.of(2026, 5, 29, 12, 0))
                .allDay(false)
                .reason("Licencia médica")
                .build();

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
        when(doctorAvailabilityBlockRepository.findByDoctorIdAndDateRange(
                userId,
                LocalDate.of(2026, 5, 1).atStartOfDay(),
                LocalDate.of(2026, 5, 31).plusDays(1).atStartOfDay()
        )).thenReturn(List.of(block));

        List<DoctorAvailabilityBlockResponse> response = doctorAvailabilityBlockService.getMyBlocks(
                userId, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getReason()).isEqualTo("Licencia médica");
    }

    @Test
    void shouldRejectBlockWhenItOverlapsFutureAppointments() {
        UUID userId = UUID.randomUUID();
        Doctor doctor = buildDoctor(userId);
        DoctorAvailabilityBlockRequest request = DoctorAvailabilityBlockRequest.builder()
                .date(LocalDate.of(2026, 5, 29))
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(12, 0))
                .reason("Licencia médica")
                .build();

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
        when(doctorAvailabilityBlockRepository.existsOverlap(any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsFutureConflict(eq(userId), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> doctorAvailabilityBlockService.createMyBlock(userId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("El bloqueo coincide con citas futuras y no puede registrarse");
    }

    @Test
    void shouldCreateAllDayBlockWhenTimesAreOmitted() {
        UUID userId = UUID.randomUUID();
        Doctor doctor = buildDoctor(userId);
        DoctorAvailabilityBlockRequest request = DoctorAvailabilityBlockRequest.builder()
                .date(LocalDate.of(2026, 5, 30))
                .reason("Congreso")
                .build();

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
        when(doctorAvailabilityBlockRepository.existsOverlap(
                eq(userId),
                eq(LocalDateTime.of(2026, 5, 30, 0, 0)),
                eq(LocalDateTime.of(2026, 5, 31, 0, 0))
        )).thenReturn(false);
        when(appointmentRepository.existsFutureConflict(eq(userId), any(), any(), any())).thenReturn(false);
        when(doctorAvailabilityBlockRepository.save(any(DoctorAvailabilityBlock.class))).thenAnswer(invocation -> {
            DoctorAvailabilityBlock block = invocation.getArgument(0);
            block.setId(UUID.randomUUID());
            return block;
        });

        DoctorAvailabilityBlockResponse response = doctorAvailabilityBlockService.createMyBlock(userId, request);

        assertThat(response.isAllDay()).isTrue();
        assertThat(response.getStartTime()).isNull();
        assertThat(response.getEndTime()).isNull();
    }

    @Test
    void shouldUpdateOwnBlock() {
        UUID userId = UUID.randomUUID();
        UUID blockId = UUID.randomUUID();
        Doctor doctor = buildDoctor(userId);
        DoctorAvailabilityBlock block = DoctorAvailabilityBlock.builder()
                .id(blockId)
                .doctor(doctor.getUser())
                .startAt(LocalDateTime.of(2026, 5, 29, 10, 0))
                .endAt(LocalDateTime.of(2026, 5, 29, 12, 0))
                .allDay(false)
                .reason("Anterior")
                .build();
        DoctorAvailabilityBlockRequest request = DoctorAvailabilityBlockRequest.builder()
                .date(LocalDate.of(2026, 5, 29))
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(13, 0))
                .reason("Licencia médica ajustada")
                .build();

        when(doctorRepository.findByUserId(userId)).thenReturn(Optional.of(doctor));
        when(doctorAvailabilityBlockRepository.findByIdAndDoctorId(blockId, userId)).thenReturn(Optional.of(block));
        when(doctorAvailabilityBlockRepository.existsOverlapExcludingId(
                userId,
                blockId,
                LocalDateTime.of(2026, 5, 29, 11, 0),
                LocalDateTime.of(2026, 5, 29, 13, 0)
        )).thenReturn(false);
        when(appointmentRepository.existsFutureConflict(eq(userId), any(), any(), any())).thenReturn(false);
        when(doctorAvailabilityBlockRepository.save(any(DoctorAvailabilityBlock.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DoctorAvailabilityBlockResponse response = doctorAvailabilityBlockService.updateMyBlock(userId, blockId, request);

        assertThat(response.getStartTime()).isEqualTo(LocalTime.of(11, 0));
        assertThat(response.getEndTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(response.getReason()).isEqualTo("Licencia médica ajustada");
    }

    @Test
    void shouldDeleteOwnBlock() {
        UUID userId = UUID.randomUUID();
        UUID blockId = UUID.randomUUID();

        when(doctorAvailabilityBlockRepository.findByIdAndDoctorId(blockId, userId))
                .thenReturn(Optional.of(DoctorAvailabilityBlock.builder().id(blockId).build()));

        doctorAvailabilityBlockService.deleteMyBlock(userId, blockId);

        ArgumentCaptor<DoctorAvailabilityBlock> captor = ArgumentCaptor.forClass(DoctorAvailabilityBlock.class);
        verify(doctorAvailabilityBlockRepository).delete(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(blockId);
    }

    @Test
    void shouldRejectDeleteWhenBlockDoesNotBelongToDoctor() {
        UUID userId = UUID.randomUUID();
        UUID blockId = UUID.randomUUID();

        when(doctorAvailabilityBlockRepository.findByIdAndDoctorId(blockId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorAvailabilityBlockService.deleteMyBlock(userId, blockId))
                .isInstanceOf(ResourceNotFoundException.class);
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
                .specialty("Cardiologia")
                .medicalLicense("MP-1001")
                .build();
    }
}
