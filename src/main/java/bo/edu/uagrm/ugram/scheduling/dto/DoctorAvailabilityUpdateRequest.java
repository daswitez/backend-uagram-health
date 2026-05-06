package bo.edu.uagrm.ugram.scheduling.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityUpdateRequest {

    @NotNull(message = "La configuración semanal es obligatoria")
    @Valid
    private List<WeeklyAvailabilitySlotRequest> weeklyAvailability;
}
