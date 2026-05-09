package bo.edu.uagrm.ugram.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlotsResponse {

    private UUID doctorId;
    private LocalDate date;
    private Integer appointmentDurationMinutes;
    private boolean readyForPublishing;
    private List<AppointmentSlotResponse> slots;
}
