package bo.edu.uagrm.ugram.identity.controller;

import bo.edu.uagrm.ugram.common.dto.ApiResponse;
import bo.edu.uagrm.ugram.identity.dto.StaffRegisterRequest;
import bo.edu.uagrm.ugram.identity.service.AdminUserService;
import bo.edu.uagrm.ugram.common.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final EmailService emailService;

    /**
     * POST /api/v1/admin/users/staff
     * Registers a new staff member (e.g. DOCTOR, LAB_TECH).
     * Protected: Only users with the ADMIN role can perform this action.
     *
     * <p>Architecture: the DB save happens inside a @Transactional method on the service.
     * When that method returns, the transaction is committed by the proxy. Only THEN do we
     * attempt to send the welcome email — this ensures SMTP I/O never runs inside an open
     * database transaction.</p>
     *
     * <p>If the email fails, the user IS still created and the response includes the
     * temporary password so the admin can share it manually.</p>
     */
    @PostMapping("/staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> registerStaff(
            @Valid @RequestBody StaffRegisterRequest request) {

        // Step 1 — Save user in DB (transaction commits when this returns)
        Map<String, String> result = adminUserService.registerStaff(request);

        String email        = result.get("email");
        String fullName     = result.get("fullName");
        String role         = result.get("role");
        String tempPassword = result.get("tempPassword");

        // Step 2 — Send email AFTER transaction committed (no open DB transaction)
        boolean emailSent = false;
        try {
            emailService.sendStaffWelcomeEmail(email, fullName, role, tempPassword);
            emailSent = true;
            log.info("Welcome email dispatched successfully to {}", email);
        } catch (Exception e) {
            log.warn("Email could not be sent to {} — user was created, admin must share credentials manually. Error: {}",
                    email, e.getMessage());
        }

        // Step 3 — Build response (always 200 — user is created regardless of email status)
        Map<String, String> responseData = new LinkedHashMap<>();
        responseData.put("email", email);
        responseData.put("role", role);
        responseData.put("tempPassword", tempPassword);
        responseData.put("emailSent", String.valueOf(emailSent));

        String message = emailSent
                ? "Personal registrado con éxito. Correo de bienvenida enviado."
                : "Personal registrado con éxito. ⚠️ El correo no pudo enviarse — comparta las credenciales manualmente.";

        return ResponseEntity.ok(ApiResponse.ok(message, responseData));
    }
}
