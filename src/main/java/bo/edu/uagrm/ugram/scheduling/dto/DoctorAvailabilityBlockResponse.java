package bo.edu.uagrm.ugram.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityBlockResponse {

    private UUID id;
    private UUID doctorUserId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean allDay;
    private String reason;
}
