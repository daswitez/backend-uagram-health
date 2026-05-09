# Estado Real del Backend

Fecha de corte: **2026-05-09**

## 1. Resumen

El backend de **UAGRM Health** ya tiene una base técnica sólida, pero la implementación funcional completa todavía se concentra en `identity/auth`.

Foto real:

- backend compila
- `./gradlew test` pasa
- ya existe una suite inicial de tests unitarios
- el modelo de datos está más adelantado que la API
- el frontend ya está integrado por rutas y roles

## 2. Aclaración de nombres

En documentación funcional usamos `UAGRM Health`, pero el código mantiene el namespace histórico `ugram`.

Ejemplos válidos hoy:

- paquete raíz: `bo.edu.uagrm.ugram`
- clase principal: `UgramHealthApplication`
- base local: `ugram_health`

Esto es intencional en la documentación para no inducir cambios técnicos inseguros.

## 3. Estado por módulo

| Módulo | Estado |
|---|---|
| `common` | implementado |
| `identity` | implementado |
| `storage` | servicio MinIO implementado, sin pipeline completo de dominio |
| `scheduling` | parcial avanzado |
| `emr` | scaffold |
| `laboratory` | scaffold |
| `notification` | planificado |

Nota sobre backlog:

- las historias documentadas son capacidades funcionales o transversales
- en desarrollo ágil pueden dividirse en varias tareas Jira
- el estado real debe validarse contra controladores, servicios, repositorios y migraciones

## 4. Lo implementado hoy

Endpoints reales:

- `POST /auth/register/patient`
- `POST /auth/login`
- `POST /auth/refresh`
- `GET /profile`
- `POST /admin/users/staff`
- `GET /doctors/me/profile`
- `PUT /doctors/me/profile`
- `GET /doctors/me/availability`
- `PUT /doctors/me/availability`
- `GET /doctors/me/schedule-settings`
- `PUT /doctors/me/schedule-settings`
- `GET /doctors/me/schedule-readiness`
- `GET /doctors/me/blocks`
- `POST /doctors/me/blocks`
- `PUT /doctors/me/blocks/{id}`
- `DELETE /doctors/me/blocks/{id}`
- `GET /calendar/holidays`
- `POST /calendar/holidays`
- `PUT /calendar/holidays/{id}`
- `DELETE /calendar/holidays/{id}`
- `GET /appointments/slots`

Notas:

- login por `email` o `ci`
- contraseña seed real: `Test_123!`
- `/profile` devuelve hoy perfil base; no completa todavía todo el DTO extendido
- el perfil profesional del médico ya puede consultarse y editarse por endpoint dedicado
- la disponibilidad semanal del médico ya puede consultarse y reemplazarse por endpoint dedicado
- los parámetros de agenda del médico ya pueden consultarse y persistirse por endpoint dedicado
- el médico ya puede consultar si su agenda está lista para publicar slots según una regla explícita de readiness
- el médico ya puede listar, registrar, editar y eliminar bloqueos puntuales con validación contra citas futuras
- el calendario institucional ya permite consultar, registrar, editar y eliminar feriados totales o jornadas parciales
- el motor de slots ya calcula disponibilidad a partir de agenda semanal, duración de consulta, feriados, bloqueos y citas activas

## 5. Lo adelantado en modelo pero no en API

### Scheduling

Ya existen:

- entidad `Appointment`
- `AppointmentRepository`
- consultas para conflictos, agenda semanal y próximas notificaciones
- entidad `DoctorWeeklyAvailability`
- entidad `DoctorScheduleSettings`
- entidad `DoctorAvailabilityBlock`
- entidad `InstitutionalHoliday`
- regla de readiness de agenda médica
- controlador y servicio para disponibilidad semanal del médico
- controlador y servicio para parámetros operativos de agenda
- controlador y servicio para readiness de agenda médica
- controlador y servicio para bloqueos puntuales del médico
- controlador y servicio para calendario institucional
- motor de generación de slots on-demand
- endpoint de consulta de slots disponibles

Falta:

- reserva, cancelación, agenda semanal y reprogramación de citas expuestas por API

### EMR

Ya existen:

- `ClinicalRecord`
- `CorrectionNote`
- `Prescription`
- `Snippet`
- `BlockchainService`
- `NoOpBlockchainService`

Falta:

- control de acceso contextual
- servicios
- controladores
- drafts clínicos

### Laboratory

Ya existen:

- entidades de catálogo, orden e items

Falta:

- repositorios
- servicios
- endpoints
- integración operativa completa con MinIO

## 6. Restricciones de seguridad que ya deben guiar el desarrollo

- mínimo privilegio
- need to know

Eso afecta:

- diseño de endpoints
- consultas
- DTOs
- pantallas por rol
- backlog

## 7. Próximo orden lógico

1. reserva y cancelación de citas
2. agenda semanal, estado operativo y reprogramación médica
3. EMR con control contextual
4. laboratorio y archivos
5. notificaciones
6. blockchain real
