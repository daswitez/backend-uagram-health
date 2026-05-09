package bo.edu.uagrm.ugram.scheduling.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityBlockRequest;
import bo.edu.uagrm.ugram.scheduling.dto.DoctorAvailabilityBlockResponse;
import bo.edu.uagrm.ugram.scheduling.service.DoctorAvailabilityBlockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/doctors/me/blocks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorAvailabilityBlockController {

    private final DoctorAvailabilityBlockService doctorAvailabilityBlockService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorAvailabilityBlockResponse>>> getMyBlocks(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<DoctorAvailabilityBlockResponse> response = doctorAvailabilityBlockService.getMyBlocks(userId, dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorAvailabilityBlockResponse>> createMyBlock(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody DoctorAvailabilityBlockRequest request) {
        DoctorAvailabilityBlockResponse response = doctorAvailabilityBlockService.createMyBlock(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Bloqueo puntual registrado", response));
    }

    @PutMapping("/{blockId}")
    public ResponseEntity<ApiResponse<DoctorAvailabilityBlockResponse>> updateMyBlock(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID blockId,
            @Valid @RequestBody DoctorAvailabilityBlockRequest request) {
        DoctorAvailabilityBlockResponse response = doctorAvailabilityBlockService.updateMyBlock(userId, blockId, request);
        return ResponseEntity.ok(ApiResponse.ok("Bloqueo puntual actualizado", response));
    }

    @DeleteMapping("/{blockId}")
    public ResponseEntity<ApiResponse<Void>> deleteMyBlock(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID blockId) {
        doctorAvailabilityBlockService.deleteMyBlock(userId, blockId);
        return ResponseEntity.ok(ApiResponse.ok("Bloqueo puntual eliminado", null));
    }
}
