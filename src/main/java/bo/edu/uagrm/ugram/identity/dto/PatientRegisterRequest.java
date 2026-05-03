package bo.edu.uagrm.ugram.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Registration request for new students.
 */
@Data
public class PatientRegisterRequest {

    @NotBlank(message = "El Carnet de Identidad (C.I.) es obligatorio")
    private String ci;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$",
             message = "La contraseña debe tener mínimo 8 caracteres, incluir al menos 1 número y 1 carácter especial")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    private String phone;
    
    // Optional patient fields
    private String career;
    private String bloodType;
}
