# Estado Real del Backend

Fecha de corte: **2026-05-05**

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
| `scheduling` | scaffold |
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

Notas:

- login por `email` o `ci`
- contraseña seed real: `Test_123!`
- `/profile` devuelve hoy perfil base; no completa todavía todo el DTO extendido

## 5. Lo adelantado en modelo pero no en API

### Scheduling

Ya existen:

- entidad `Appointment`
- `AppointmentRepository`
- consultas para conflictos, agenda semanal y próximas notificaciones

Falta:

- disponibilidad médica
- feriados
- bloqueos del médico
- motor de slots
- servicios y controladores

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

1. perfil profesional del médico
2. agenda y disponibilidad
3. feriados y bloqueos
4. slots y citas
5. EMR con control contextual
6. laboratorio y archivos
7. notificaciones
8. blockchain real
