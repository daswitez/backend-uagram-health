package bo.edu.uagrm.ugram.identity.dto;

import bo.edu.uagrm.ugram.identity.entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StaffRegisterRequest {

    @NotBlank(message = "El Carnet de Identidad (C.I.) es obligatorio")
    private String ci;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    private String phone;

    @NotNull(message = "El rol es obligatorio (ej: DOCTOR o LAB_TECH)")
    private UserType userType;

    // Doctor specific fields
    private String medicalLicense;
    private String specialty;
}
