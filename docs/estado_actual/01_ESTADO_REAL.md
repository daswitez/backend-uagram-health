# Estado Real del Backend

Fecha de corte: **2026-05-06**

## 1. Resumen

El backend de **UAGRM Health** ya tiene una base técnica sólida, pero la implementación funcional completa todavía se concentra en `identity/auth`.

Foto real:

- backend compila
- `./gradlew test` pasa
- no existe `src/test`
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
| `storage` | parcial |
| `scheduling` | parcial |
| `emr` | scaffold |
| `laboratory` | scaffold |
| `notification` | planificado |

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

Notas:

- login por `email` o `ci`
- contraseña seed real: `Test_123!`
- `/profile` devuelve hoy perfil base; no completa todavía todo el DTO extendido
- el perfil profesional del médico ya puede consultarse y editarse por endpoint dedicado
- la disponibilidad semanal del médico ya puede consultarse y reemplazarse por endpoint dedicado

## 5. Lo adelantado en modelo pero no en API

### Scheduling

Ya existen:

- entidad `Appointment`
- `AppointmentRepository`
- consultas para conflictos, agenda semanal y próximas notificaciones
- entidad `DoctorWeeklyAvailability`
- controlador y servicio para disponibilidad semanal del médico

Falta:

- parámetros operativos de agenda
- feriados
- bloqueos del médico
- motor de slots
- slots y citas expuestos por API

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

1. parámetros operativos de agenda
2. feriados y bloqueos
3. slots y citas
4. EMR con control contextual
5. laboratorio y archivos
6. notificaciones
7. blockchain real
