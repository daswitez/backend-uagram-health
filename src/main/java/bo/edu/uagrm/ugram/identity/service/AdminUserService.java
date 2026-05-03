package bo.edu.uagrm.ugram.identity.service;

import bo.edu.uagrm.ugram.common.exception.BusinessException;
import bo.edu.uagrm.ugram.identity.dto.StaffRegisterRequest;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.entity.UserType;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.identity.repository.UserRepository;
import bo.edu.uagrm.ugram.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Registers a new staff member and attempts to send welcome credentials by email.
     *
     * <p>Architecture notes:
     * <ul>
     *   <li>The DB write happens inside {@code @Transactional} on this method.</li>
     *   <li>The email is sent AFTER the method returns and the transaction commits,
     *       orchestrated by the controller layer.</li>
     * </ul>
     *
     * @return a map with {@code tempPassword}, {@code email}, {@code fullName}, and {@code role}
     *         so the controller can dispatch the email outside the transaction boundary
     *         and include the password in the HTTP response as a fallback.
     */
    @Transactional
    public Map<String, String> registerStaff(StaffRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo electrónico ya está registrado");
        }
        if (userRepository.existsByCi(request.getCi())) {
            throw new BusinessException("El Carnet de Identidad (C.I.) ya está registrado");
        }
        if (request.getUserType() == UserType.STUDENT || request.getUserType() == UserType.PATIENT) {
            throw new BusinessException("Este endpoint es solo para registro de personal médico/administrativo");
        }

        // Generate a cryptographically secure temporary password that always satisfies:
        // ^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$
        String tempPassword = generateTempPassword();

        User user = User.builder()
                .email(request.getEmail())
                .ci(request.getCi())
                .passwordHash(passwordEncoder.encode(tempPassword))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .userType(request.getUserType())
                .isActive(true)
                .build();

        user = userRepository.save(user);

        // If it's a doctor, we need to create the doctor profile
        if (request.getUserType() == UserType.DOCTOR) {
            if (request.getMedicalLicense() == null || request.getMedicalLicense().isBlank()) {
                throw new BusinessException("La Matrícula Profesional es requerida para registrar un Médico");
            }
            if (request.getSpecialty() == null || request.getSpecialty().isBlank()) {
                throw new BusinessException("La Especialidad es requerida para registrar un Médico");
            }
            if (doctorRepository.existsByMedicalLicense(request.getMedicalLicense())) {
                throw new BusinessException("La Matrícula Profesional ya está registrada");
            }

            Doctor doctor = Doctor.builder()
                    .user(user)
                    .medicalLicense(request.getMedicalLicense())
                    .specialty(request.getSpecialty())
                    .build();

            doctorRepository.save(doctor);
        }

        log.info("Staff user persisted: {} (Role: {})", user.getEmail(), user.getUserType());

        // Return the data needed to send the email from the controller
        // (AFTER this @Transactional method returns and the transaction commits)
        Map<String, String> result = new LinkedHashMap<>();
        result.put("tempPassword", tempPassword);
        result.put("email", user.getEmail());
        result.put("fullName", user.getFirstName() + " " + user.getLastName());
        result.put("role", user.getUserType().name());
        return result;
    }

    /**
     * Generates a cryptographically secure temporary password that always satisfies
     * the platform's password policy: ^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$
     *
     * <p>The password will be 12 characters long and contain at least:
     * <ul>
     *   <li>2 uppercase letters</li>
     *   <li>2 lowercase letters</li>
     *   <li>2 digits (0-9)</li>
     *   <li>2 special characters from {@code !@#$%^&*}</li>
     *   <li>4 additional characters from the full alphanumeric pool</li>
     * </ul>
     * Characters are shuffled so required types don't appear at predictable positions.
     */
    private String generateTempPassword() {
        final SecureRandom random = new SecureRandom();
        final String upper   = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower   = "abcdefghijklmnopqrstuvwxyz";
        final String digits  = "0123456789";
        final String special = "!@#$%^&*";
        final String allChars = upper + lower + digits + special;

        List<Character> chars = new ArrayList<>(12);

        // Guarantee at least 2 of each required type
        for (int i = 0; i < 2; i++) chars.add(upper.charAt(random.nextInt(upper.length())));
        for (int i = 0; i < 2; i++) chars.add(lower.charAt(random.nextInt(lower.length())));
        for (int i = 0; i < 2; i++) chars.add(digits.charAt(random.nextInt(digits.length())));
        for (int i = 0; i < 2; i++) chars.add(special.charAt(random.nextInt(special.length())));

        // Fill remaining 4 positions from the full pool
        for (int i = 0; i < 4; i++) chars.add(allChars.charAt(random.nextInt(allChars.length())));

        // Shuffle to avoid predictable patterns (e.g. always starting with uppercase)
        Collections.shuffle(chars, random);

        StringBuilder sb = new StringBuilder(12);
        for (char c : chars) sb.append(c);
        return sb.toString();
    }
}
