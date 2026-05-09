package bo.edu.uagrm.ugram.scheduling.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.scheduling.dto.InstitutionalHolidayRequest;
import bo.edu.uagrm.ugram.scheduling.dto.InstitutionalHolidayResponse;
import bo.edu.uagrm.ugram.scheduling.service.InstitutionalHolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/calendar/holidays")
@RequiredArgsConstructor
public class InstitutionalHolidayController {

    private final InstitutionalHolidayService institutionalHolidayService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<List<InstitutionalHolidayResponse>>> getHolidays(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        List<InstitutionalHolidayResponse> response = institutionalHolidayService.getHolidays(dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InstitutionalHolidayResponse>> createHoliday(
            @Valid @RequestBody InstitutionalHolidayRequest request) {
        InstitutionalHolidayResponse response = institutionalHolidayService.createHoliday(request);
        return ResponseEntity.ok(ApiResponse.ok("Restricción institucional registrada", response));
    }

    @PutMapping("/{holidayId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<InstitutionalHolidayResponse>> updateHoliday(
            @PathVariable UUID holidayId,
            @Valid @RequestBody InstitutionalHolidayRequest request) {
        InstitutionalHolidayResponse response = institutionalHolidayService.updateHoliday(holidayId, request);
        return ResponseEntity.ok(ApiResponse.ok("Restricción institucional actualizada", response));
    }

    @DeleteMapping("/{holidayId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteHoliday(@PathVariable UUID holidayId) {
        institutionalHolidayService.deleteHoliday(holidayId);
        return ResponseEntity.ok(ApiResponse.ok("Restricción institucional eliminada", null));
    }
}
