package bo.edu.uagrm.ugram.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {

    private UUID userId;
    private UUID doctorId;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String specialty;
    private String medicalLicense;
}
