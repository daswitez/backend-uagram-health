package bo.edu.uagrm.ugram.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StaffAccountUpdateRequest {

    @NotBlank(message = "El Carnet de Identidad (C.I.) es obligatorio")
    @Size(max = 50, message = "El C.I. no puede exceder 50 caracteres")
    private String ci;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String lastName;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String phone;

    @NotNull(message = "El estado activo de la cuenta es obligatorio")
    private Boolean active;

    @Size(max = 100, message = "La matrícula profesional no puede exceder 100 caracteres")
    private String medicalLicense;

    @Size(max = 200, message = "La especialidad no puede exceder 200 caracteres")
    private String specialty;
}
