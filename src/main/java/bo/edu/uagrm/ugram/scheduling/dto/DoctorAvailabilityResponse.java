package bo.edu.uagrm.ugram.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorAvailabilityResponse {

    private UUID doctorUserId;
    private List<WeeklyAvailabilitySlotResponse> weeklyAvailability;
}
