package bo.edu.uagrm.ugram.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendStaffWelcomeEmail(String toEmail, String fullName, String role, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Bienvenido a Ugram Health - Credenciales de Acceso");

            String body = String.format(
                "Estimado/a %s,\n\n" +
                "Su cuenta de %s en Ugram Health ha sido creada exitosamente.\n\n" +
                "Sus credenciales temporales de acceso son:\n" +
                "Usuario: %s\n" +
                "Contraseña temporal: %s\n\n" +
                "Por favor, inicie sesión en la plataforma y cambie esta contraseña inmediatamente por su propia seguridad.\n\n" +
                "Saludos cordiales,\n" +
                "Administración Ugram Health",
                fullName, role, toEmail, tempPassword
            );

            message.setText(body);
            mailSender.send(message);
            log.info("Welcome email sent successfully to {}", toEmail);

        } catch (Exception e) {
            log.error("SMTP ERROR — Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar el correo de bienvenida a " + toEmail + ". Revise la configuración SMTP. Causa: " + e.getMessage(), e);
        }
    }
}
