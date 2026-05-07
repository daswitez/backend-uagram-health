package bo.edu.uagrm.ugram.scheduling.dto;

import bo.edu.uagrm.ugram.scheduling.entity.InstitutionalHolidayType;
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
public class InstitutionalHolidayResponse {

    private UUID id;
    private LocalDate date;
    private InstitutionalHolidayType type;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
}
