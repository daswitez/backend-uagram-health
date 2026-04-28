package bo.edu.uagrm.ugram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ugram Health Backend — Entry Point
 *
 * Monolito Modular con 6 módulos de dominio:
 * - identity:      Autenticación JWT, usuarios, RBAC
 * - scheduling:    Gestión de citas, Kanban calendar
 * - emr:           Historia clínica electrónica, blockchain
 * - laboratory:    Órdenes de laboratorio, batch approval
 * - notification:  Notificaciones push
 * - storage:       Almacenamiento MinIO
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class UgramHealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(UgramHealthApplication.class, args);
    }
}
