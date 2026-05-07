package bo.edu.uagrm.ugram.scheduling.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorScheduleSettingsRequest;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorScheduleSettingsResponse;
import bo.edu.uagrm.ugram.scheduling.service.DoctorScheduleSettingsService;
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
@RequestMapping("/v1/doctors/me/schedule-settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorScheduleSettingsController {

    private final DoctorScheduleSettingsService doctorScheduleSettingsService;

    @GetMapping
    public ResponseEntity<ApiResponse<DoctorScheduleSettingsResponse>> getMyScheduleSettings(
            @AuthenticationPrincipal UUID userId) {
        DoctorScheduleSettingsResponse response = doctorScheduleSettingsService.getMyScheduleSettings(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<DoctorScheduleSettingsResponse>> updateMyScheduleSettings(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody DoctorScheduleSettingsRequest request) {
        DoctorScheduleSettingsResponse response = doctorScheduleSettingsService.updateMyScheduleSettings(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Parámetros de agenda actualizados", response));
    }
}
