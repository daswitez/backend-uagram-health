package bo.edu.uagrm.ugram.scheduling.repository;

import bo.edu.uagrm.ugram.scheduling.entity.Appointment;
import bo.edu.uagrm.ugram.scheduling.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Weekly view for Kanban-Calendar (US-W03).
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.scheduledStart BETWEEN :weekStart AND :weekEnd " +
           "ORDER BY a.scheduledStart")
    List<Appointment> findDoctorWeeklyAppointments(
            @Param("doctorId") UUID doctorId,
            @Param("weekStart") LocalDateTime weekStart,
            @Param("weekEnd") LocalDateTime weekEnd);

    /**
     * Patient's upcoming appointments (US-M05).
     */
    List<Appointment> findByPatientIdAndStatusInOrderByScheduledStartAsc(
            UUID patientId, List<AppointmentStatus> statuses);

    /**
     * Find all appointments needing notification (1 hour before).
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE a.status = 'SCHEDULED' " +
           "AND a.scheduledStart BETWEEN :from AND :to")
    List<Appointment> findUpcomingForNotification(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    /**
     * Check for scheduling conflicts.
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
           "AND a.scheduledStart < :end " +
           "AND a.scheduledEnd > :start")
    boolean existsConflict(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
           "AND a.scheduledStart < :end " +
           "AND a.scheduledEnd > :start " +
           "AND a.scheduledEnd > :now")
    boolean existsFutureConflict(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("now") LocalDateTime now);

    @Query("SELECT a FROM Appointment a " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
           "AND a.scheduledStart < :end " +
           "AND a.scheduledEnd > :start " +
           "ORDER BY a.scheduledStart")
    List<Appointment> findActiveByDoctorIdAndRange(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);
}
