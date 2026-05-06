package bo.edu.uagrm.ugram.identity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileUpdateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String lastName;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;

    @NotBlank(message = "La especialidad es obligatoria")
    @Size(max = 200, message = "La especialidad no puede exceder 200 caracteres")
    private String specialty;

    @NotBlank(message = "La matrícula profesional es obligatoria")
    @Size(max = 100, message = "La matrícula profesional no puede exceder 100 caracteres")
    private String medicalLicense;
}
