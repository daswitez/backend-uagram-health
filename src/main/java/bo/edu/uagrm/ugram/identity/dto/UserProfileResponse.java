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
public class UserProfileResponse {

    private UUID id;
    private String email;
    private String ru;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String userType;
    private boolean active;

    // ── Student-specific fields (nullable for staff) ─────
    private String career;
    private String bloodType;
    private String dateOfBirth;

    // ── Doctor-specific fields (nullable for non-doctors) ─
    private String specialty;
    private String medicalLicense;
}
