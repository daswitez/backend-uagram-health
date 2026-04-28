# Ugram Health: Backlog Priorizado y Roadmap de Desarrollo

Este documento define el estado actual del proyecto, el backlog priorizado con criterios de aceptación (formato BDD para Test-Driven Development) y el roadmap a seguir para alcanzar un sistema completo y adecuado.

---

## 📍 1. Estado Actual: ¿Qué tenemos?

Actualmente, hemos completado la **Fase 1 (Cimientos y Arquitectura Base)** del Backend.

**Infraestructura y Configuración:**
- [x] Docker Compose configurado (PostgreSQL 16, MinIO).
- [x] Spring Boot 3.4 con Java 21 configurado (Gradle).
- [x] Migraciones de base de datos automatizadas con Flyway (Esquemas completos).
- [x] Configuración de Seguridad Central (Stateless JWT, CORS, BCrypt).
- [x] Manejo global de excepciones (`GlobalExceptionHandler`).

**Módulos de Dominio (Scaffold):**
- [x] **`common`**: Excepciones, DTOs de respuesta unificada, utilidades criptográficas.
- [x] **`identity`**: Entidades, Repositorios, Servicios (Login, Refresh, Profile) y Controladores implementados. **(100% Funcional)**.
- [x] **`scheduling`**: Entidades y Repositorios listos. *(Falta Lógica y Endpoints)*.
- [x] **`emr`**: Entidades e Interfaz Blockchain listos. *(Falta Lógica y Endpoints)*.
- [x] **`laboratory`**: Entidades listas. *(Falta Lógica y Endpoints)*.
- [x] **`storage`**: Servicio de MinIO implementado. *(Faltan Endpoints)*.

---

## 🛤️ 2. El Camino a Seguir (Roadmap de Ejecución)

El enfoque de desarrollo será estrictamente iterativo y orientado a pruebas (TDD). No se construirá el Frontend hasta que el endpoint correspondiente del Backend tenga sus pruebas unitarias y de integración pasando en verde.

1.  **Fase 2: Gestión de Citas (Scheduling)**
    *   Finalizar el CRUD de citas.
    *   Desarrollar vistas para el "Kanban Calendar" (Web) y la reserva de turnos (Móvil).
2.  **Fase 3: EMR (Historia Clínica) y Recetas**
    *   Implementar el guardado inmutable de fichas médicas.
    *   Integración con macros/snippets (`/gripe`).
3.  **Fase 4: Módulo de Laboratorio y Storage**
    *   Subida de PDFs a MinIO y generación de reportes.
    *   Aprobación por lotes (Batch Approval).
4.  **Fase 5: Blockchain y Notificaciones (Finalización)**
    *   Sustituir `NoOpBlockchainService` por la integración real con Hyperledger.
    *   Implementar notificaciones Push (Firebase).

---

## 📋 3. Backlog Priorizado (Orientado a Pruebas / TDD)

Las historias de usuario están priorizadas por dependencia técnica. Cada historia incluye **Criterios de Aceptación (AC)** en formato BDD (*Given/When/Then*) para guiar la creación de tests unitarios antes de codificar la solución.

### 🟡 PRIORIDAD 1: Módulo Scheduling (Agendamiento)

#### US-S01: Listado de Horarios Disponibles (Slots)
*Como estudiante, quiero ver los horarios disponibles de los médicos para poder agendar una cita.*

*   **AC 1: Slots disponibles estándar**
    *   *Given* un médico con horario de 8:00 a 12:00,
    *   *When* el estudiante consulta disponibilidad para el día X,
    *   *Then* el sistema devuelve bloques de 20 minutos que no estén ocupados ni cancelados.
*   **Enfoque TDD:** Crear test `getAvailableSlots_ReturnsEmptySlotsOnly()`. Mockear base de datos devolviendo 1 cita ocupada y verificar que el bloque de esa cita se excluya del resultado.

#### US-S02: Creación de Citas con Prevención de Conflictos
*Como estudiante, quiero reservar una cita recibiendo confirmación sin que se solape con otro paciente.*

*   **AC 1: Prevención de solapamiento (Double-booking)**
    *   *Given* un médico que ya tiene una cita de 10:00 a 10:20,
    *   *When* el estudiante B intenta agendar a las 10:00,
    *   *Then* el sistema rechaza la solicitud lanzando `BusinessException` ("Horario no disponible").
*   **Enfoque TDD:** Escribir test `createAppointment_WhenConflictExists_ThrowsException()`. Probar validación cruzada en la capa de Servicio.

#### US-S03: Kanban de Citas para Médicos
*Como médico, quiero ver todas mis citas del día o semana para gestionar mi flujo de pacientes.*

*   **AC 1: Filtro por estado y fecha**
    *   *Given* un médico autenticado,
    *   *When* consulta su agenda de la semana actual,
    *   *Then* recibe todas las citas agrupadas por día y ordenadas cronológicamente, incluyendo datos básicos del paciente.
*   **Enfoque TDD:** Test de repositorio `findDoctorWeeklyAppointments()` verificando que el filtrado por fechas (BETWEEN) funciona correctamente.

---

### 🟠 PRIORIDAD 2: Módulo EMR (Historia Clínica)

#### US-E01: Guardado de Ficha Clínica (Inmutable)
*Como médico, quiero guardar el diagnóstico y plan de tratamiento para que quede en el historial.*

*   **AC 1: Generación de Hash y Cifrado**
    *   *Given* un diagnóstico válido de un paciente,
    *   *When* el médico guarda la ficha,
    *   *Then* el sistema encripta el texto plano (AES), genera un Hash SHA-256 del original, lo guarda en BD (sin `updated_at`) y llama al `BlockchainService.anchorHash()`.
*   **Enfoque TDD:** Testear `ClinicalRecordService.saveRecord()`. Verificar con `Mockito.verify()` que `EncryptionUtil.encrypt()` y `BlockchainService.anchorHash()` fueron llamados exactamente 1 vez.

#### US-E02: Creación y Recuperación de Snippets (Macros)
*Como médico, quiero usar atajos (ej. /gripe) para autocompletar planes de tratamiento estándar.*

*   **AC 1: Búsqueda de snippets activos**
    *   *Given* la base de datos de macros,
    *   *When* el médico consulta los snippets,
    *   *Then* el sistema devuelve la lista completa de macros `is_active = true` con su trigger y contenido.
*   **Enfoque TDD:** Escribir un controlador simple, testear con `MockMvc` que el JSON retornado contiene el formato esperado para el frontend (Next.js).

---

### 🔴 PRIORIDAD 3: Laboratorio y Archivos (Storage)

#### US-L01: Subida de Resultados (PDF/DICOM)
*Como personal de laboratorio, quiero subir un resultado en PDF a la orden de un paciente.*

*   **AC 1: Integración exitosa con MinIO**
    *   *Given* una orden de laboratorio en estado PENDING y un archivo PDF válido,
    *   *When* el laboratorista sube el archivo,
    *   *Then* el archivo se guarda en MinIO, se actualiza la BD con la URL del archivo, y la orden pasa a estado READY.
*   **Enfoque TDD:** Mockear `MinioClient`. Escribir test comprobando que el estado de la entidad `LabOrder` cambia a `READY` tras la subida del archivo.

#### US-L02: Aprobación por Lotes (Batch Approval)
*Como laboratorista, quiero seleccionar múltiples exámenes normales y aprobarlos de un solo clic.*

*   **AC 1: Aprobación masiva**
    *   *Given* una lista de IDs de órdenes de laboratorio `[1, 2, 3]`,
    *   *When* el usuario envía la solicitud de aprobación por lotes,
    *   *Then* el sistema cambia el estado de todas las órdenes a APPROVED en una sola transacción de base de datos.
*   **Enfoque TDD:** Escribir test `@Transactional`. Guardar 3 órdenes PENDING, ejecutar el servicio y verificar que en BD las 3 ahora están en APPROVED.

---

### 🔵 PRIORIDAD 4: Notificaciones y Background Jobs

#### US-N01: Recordatorio de Citas (Cron Job)
*Como sistema, quiero enviar notificaciones 1 hora antes de la cita para reducir el ausentismo.*

*   **AC 1: Disparo de notificaciones a tiempo**
    *   *Given* una cita agendada para las 10:00 AM,
    *   *When* el reloj del servidor marca las 09:00 AM y corre el Cron Job programado,
    *   *Then* se envía un evento de notificación push al dispositivo del estudiante.
*   **Enfoque TDD:** Testear la query del repositorio `findUpcomingForNotification()`. Verificar que selecciona correctamente citas que están a exactamente 1 hora en el futuro, descartando las canceladas.
