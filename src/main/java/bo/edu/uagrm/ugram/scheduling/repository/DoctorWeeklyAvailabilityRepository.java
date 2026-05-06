package bo.edu.uagrm.ugram.scheduling.repository;

import bo.edu.uagrm.ugram.scheduling.entity.DoctorWeeklyAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoctorWeeklyAvailabilityRepository extends JpaRepository<DoctorWeeklyAvailability, UUID> {
    List<DoctorWeeklyAvailability> findByDoctorId(UUID doctorId);
    void deleteByDoctorId(UUID doctorId);
}
