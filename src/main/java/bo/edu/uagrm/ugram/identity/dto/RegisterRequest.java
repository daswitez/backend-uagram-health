package bo.edu.uagrm.ugram.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Registration request for new students.
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "El Registro Universitario (R.U.) es obligatorio")
    private String ru;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "Debe ser un correo válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
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
