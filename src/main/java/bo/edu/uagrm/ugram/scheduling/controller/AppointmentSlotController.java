package bo.edu.uagrm.ugram.scheduling.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.scheduling.dto.AppointmentSlotsResponse;
import bo.edu.uagrm.ugram.scheduling.service.AppointmentSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/v1/appointments/slots")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STUDENT', 'DOCTOR', 'ADMIN')")
public class AppointmentSlotController {

    private final AppointmentSlotService appointmentSlotService;

    @GetMapping
    public ResponseEntity<ApiResponse<AppointmentSlotsResponse>> getAvailableSlots(
            @RequestParam UUID doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        AppointmentSlotsResponse response = appointmentSlotService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
