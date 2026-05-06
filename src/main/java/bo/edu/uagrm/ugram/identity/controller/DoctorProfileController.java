package bo.edu.uagrm.ugram.identity.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.identity.dto.DoctorProfileResponse;
import bo.edu.uagrm.ugram.identity.dto.DoctorProfileUpdateRequest;
import bo.edu.uagrm.ugram.identity.service.DoctorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/doctors/me")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorProfileController {

    private final DoctorProfileService doctorProfileService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UUID userId) {
        DoctorProfileResponse profile = doctorProfileService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody DoctorProfileUpdateRequest request) {
        DoctorProfileResponse profile = doctorProfileService.updateMyProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Perfil profesional actualizado", profile));
    }
}
