package bo.edu.uagrm.ugram.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentSlotResponse {

    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
