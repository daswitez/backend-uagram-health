package bo.edu.uagrm.ugram.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffAccountResponse {

    private UUID id;
    private String email;
    private String ci;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String userType;
    private boolean active;
    private String specialty;
    private String medicalLicense;
    private Instant createdAt;
    private Instant updatedAt;
}
