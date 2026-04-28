package bo.edu.uagrm.ugram.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Login request — supports both email (staff) and RU (students).
 * The backend resolves the identifier type automatically.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "El identificador (email o R.U.) es obligatorio")
    private String identifier;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
