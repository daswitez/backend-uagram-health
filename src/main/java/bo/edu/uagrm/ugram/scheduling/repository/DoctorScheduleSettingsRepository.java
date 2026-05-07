package bo.edu.uagrm.ugram.scheduling.repository;

import bo.edu.uagrm.ugram.scheduling.entity.DoctorScheduleSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorScheduleSettingsRepository extends JpaRepository<DoctorScheduleSettings, UUID> {
    Optional<DoctorScheduleSettings> findByDoctorId(UUID doctorId);
}
