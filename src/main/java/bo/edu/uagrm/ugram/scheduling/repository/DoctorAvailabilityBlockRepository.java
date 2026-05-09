package bo.edu.uagrm.ugram.scheduling.repository;

import bo.edu.uagrm.ugram.scheduling.entity.DoctorAvailabilityBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorAvailabilityBlockRepository extends JpaRepository<DoctorAvailabilityBlock, UUID> {

    @Query("SELECT COUNT(b) > 0 FROM DoctorAvailabilityBlock b " +
           "WHERE b.doctor.id = :doctorId " +
           "AND b.startAt < :end " +
           "AND b.endAt > :start")
    boolean existsOverlap(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(b) > 0 FROM DoctorAvailabilityBlock b " +
           "WHERE b.doctor.id = :doctorId " +
           "AND b.id <> :blockId " +
           "AND b.startAt < :end " +
           "AND b.endAt > :start")
    boolean existsOverlapExcludingId(
            @Param("doctorId") UUID doctorId,
            @Param("blockId") UUID blockId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT b FROM DoctorAvailabilityBlock b " +
           "WHERE b.doctor.id = :doctorId " +
           "AND b.startAt < :end " +
           "AND b.endAt > :start " +
           "ORDER BY b.startAt")
    List<DoctorAvailabilityBlock> findByDoctorIdAndDateRange(
            @Param("doctorId") UUID doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<DoctorAvailabilityBlock> findByDoctorIdOrderByStartAtAsc(UUID doctorId);

    Optional<DoctorAvailabilityBlock> findByIdAndDoctorId(UUID id, UUID doctorId);
}
