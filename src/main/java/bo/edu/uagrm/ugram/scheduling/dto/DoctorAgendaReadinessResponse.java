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
public class DoctorAgendaReadinessResponse {

    private UUID doctorUserId;
    private boolean profileComplete;
    private boolean weeklyAvailabilityConfigured;
    private boolean scheduleSettingsConfigured;
    private boolean readyForPublishing;
    private List<String> missingRequirements;
}
