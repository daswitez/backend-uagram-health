package bo.edu.uagrm.ugram.scheduling.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityResponse;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityUpdateRequest;
import bo.edu.uagrm.ugram.scheduling.service.DoctorAvailabilityService;
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
@RequestMapping("/v1/doctors/me/availability")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService doctorAvailabilityService;

    @GetMapping
    public ResponseEntity<ApiResponse<DoctorAvailabilityResponse>> getMyAvailability(
            @AuthenticationPrincipal UUID userId) {
        DoctorAvailabilityResponse response = doctorAvailabilityService.getMyAvailability(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<DoctorAvailabilityResponse>> updateMyAvailability(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody DoctorAvailabilityUpdateRequest request) {
        DoctorAvailabilityResponse response = doctorAvailabilityService.updateMyAvailability(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad semanal actualizada", response));
    }
}
