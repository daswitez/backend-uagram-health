package bo.edu.uagrm.ugram.identity.service;

import bo.edu.uagrm.ugram.common.exception.BusinessException;
import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.identity.dto.DoctorProfileResponse;
import bo.edu.uagrm.ugram.identity.dto.DoctorProfileUpdateRequest;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorProfileService {

    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    public DoctorProfileResponse getMyProfile(UUID userId) {
        Doctor doctor = findDoctorByUserId(userId);
        return mapToResponse(doctor);
    }

    @Transactional
    public DoctorProfileResponse updateMyProfile(UUID userId, DoctorProfileUpdateRequest request) {
        Doctor doctor = findDoctorByUserId(userId);
        String normalizedLicense = request.getMedicalLicense().trim();

        if (doctorRepository.existsByMedicalLicenseAndUserIdNot(normalizedLicense, userId)) {
            throw new BusinessException("La Matrícula Profesional ya está registrada");
        }

        User user = doctor.getUser();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhone(normalizePhone(request.getPhone()));

        doctor.setSpecialty(request.getSpecialty().trim());
        doctor.setMedicalLicense(normalizedLicense);

        return mapToResponse(doctorRepository.save(doctor));
    }

    private Doctor findDoctorByUserId(UUID userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "userId", userId));
    }

    private DoctorProfileResponse mapToResponse(Doctor doctor) {
        User user = doctor.getUser();
        return DoctorProfileResponse.builder()
                .userId(user.getId())
                .doctorId(doctor.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .specialty(doctor.getSpecialty())
                .medicalLicense(doctor.getMedicalLicense())
                .build();
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        String normalized = phone.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
