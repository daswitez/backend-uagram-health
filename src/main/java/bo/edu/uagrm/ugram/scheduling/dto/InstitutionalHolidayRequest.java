package bo.edu.uagrm.ugram.scheduling.dto;

import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHolidayType;
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
public class InstitutionalHolidayRequest {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "El tipo es obligatorio")
    private InstitutionalHolidayType type;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotBlank(message = "La razón es obligatoria")
    private String reason;
}
