package bo.edu.uagrm.ugram.scheduling.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorScheduleSettingsRequest {

    @NotNull(message = "La duración de consulta es obligatoria")
    @Min(value = 10, message = "La duración mínima permitida es 10 minutos")
    @Max(value = 120, message = "La duración máxima permitida es 120 minutos")
    private Integer appointmentDurationMinutes;
}
