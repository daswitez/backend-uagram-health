package bo.edu.uagrm.ugram.scheduling.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAgendaReadinessResponse;
import bo.edu.uagrm.ugram.scheduling.service.DoctorAgendaReadinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/doctors/me/schedule-readiness")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorAgendaReadinessController {

    private final DoctorAgendaReadinessService doctorAgendaReadinessService;

    @GetMapping
    public ResponseEntity<ApiResponse<DoctorAgendaReadinessResponse>> getMyAgendaReadiness(
            @AuthenticationPrincipal UUID userId) {
        DoctorAgendaReadinessResponse response = doctorAgendaReadinessService.getMyAgendaReadiness(userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
