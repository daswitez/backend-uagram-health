package bo.edu.uagrm.ugram.identity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Refresh token request — used to obtain a new access token.
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;
}
