package bo.edu.uagrm.ugram.identity.repository;

import bo.edu.uagrm.ugram.identity.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    Optional<Doctor> findByUserId(UUID userId);
    boolean existsByMedicalLicense(String medicalLicense);
    boolean existsByMedicalLicenseAndUserIdNot(String medicalLicense, UUID userId);
}
