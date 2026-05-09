package bo.edu.uagrm.ugram.scheduling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityBlockRequest {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotBlank(message = "La razón es obligatoria")
    private String reason;
}
