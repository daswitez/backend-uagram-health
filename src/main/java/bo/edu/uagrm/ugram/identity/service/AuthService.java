package bo.edu.uagrm.ugram.identity.service;

import bo.edu.uagrm.ugram.common.exception.BusinessException;
import bo.edu.uagrm.ugram.common.exception.ResourceNotFoundException;
import bo.edu.uagrm.ugram.common.security.JwtProvider;
import bo.edu.uagrm.ugram.identity.dto.*;
import bo.edu.uagrm.ugram.identity.entity.Doctor;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.entity.UserType;
import bo.edu.uagrm.ugram.identity.entity.Patient;
import bo.edu.uagrm.ugram.identity.repository.DoctorRepository;
import bo.edu.uagrm.ugram.identity.repository.UserRepository;
import bo.edu.uagrm.ugram.identity.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${app.jwt.access-expiration-ms}")
    private long accessExpirationMs;

    /**
     * Registers a new student (User + Patient profile).
     */
    @Transactional
    public void register(PatientRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El correo electrónico ya está registrado");
        }
        if (userRepository.existsByCi(request.getCi())) {
            throw new BusinessException("El Carnet de Identidad (C.I.) ya está registrado");
        }

        User user = User.builder()
                .email(request.getEmail())
                .ci(request.getCi())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .userType(UserType.STUDENT)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(user)
                .career(request.getCareer())
                .bloodType(request.getBloodType())
                .build();

        patientRepository.save(patient);

        log.info("Student registered successfully: {} (CI: {})", user.getEmail(), user.getCi());
    }

    /**
     * Authenticates a user by email or CI and returns JWT tokens.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmailOrCi(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!user.getIsActive()) {
            throw new BusinessException("Tu cuenta está desactivada. Contacta a administración.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getUserType().name());
        String refreshToken = jwtProvider.generateRefreshToken(
                user.getId(), user.getEmail(), user.getUserType().name());

        log.info("User {} ({}) logged in successfully", user.getEmail(), user.getUserType());

        return buildLoginResponse(user, accessToken, refreshToken);
    }

    /**
     * Refreshes an access token using a valid refresh token.
     */
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        if (!jwtProvider.validateToken(token)) {
            throw new BadCredentialsException("Refresh token inválido o expirado");
        }

        String tokenType = jwtProvider.getTokenType(token);
        if (!"refresh".equals(tokenType)) {
            throw new BadCredentialsException("Token proporcionado no es un refresh token");
        }

        UUID userId = jwtProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        String newAccessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getUserType().name());
        String newRefreshToken = jwtProvider.generateRefreshToken(
                user.getId(), user.getEmail(), user.getUserType().name());

        return buildLoginResponse(user, newAccessToken, newRefreshToken);
    }

    /**
     * Returns the profile of the currently authenticated user.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapToProfile(user);
    }

    private UserProfileResponse mapToProfile(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .ci(user.getCi())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .userType(user.getUserType().name())
                .active(user.getIsActive())
                .build();
    }

    private LoginResponse buildLoginResponse(User user, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessExpirationMs / 1000)
                .userType(user.getUserType().name())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .specialty(resolveSpecialty(user))
                .user(mapToProfile(user))
                .build();
    }

    private String resolveSpecialty(User user) {
        if (user.getUserType() != UserType.DOCTOR) {
            return null;
        }

        return doctorRepository.findByUserId(user.getId())
                .map(Doctor::getSpecialty)
                .orElse(null);
    }
}
