package bo.edu.uagrm.ugram.identity.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.identity.dto.*;
import bo.edu.uagrm.ugram.identity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/login
     * Authenticates user by email or R.U. and returns JWT tokens.
     * US-M01, US-W01
     */
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Inicio de sesión exitoso", response));
    }

    /**
     * POST /api/v1/auth/refresh
     * Exchanges a refresh token for a new access + refresh token pair.
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Token renovado", response));
    }

    /**
     * GET /api/v1/profile
     * Returns the authenticated user's profile.
     * US-M03, US-M04
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UUID userId) {
        UserProfileResponse profile = authService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }
}
