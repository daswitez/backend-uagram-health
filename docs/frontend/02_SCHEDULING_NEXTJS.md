# Frontend Next.js - Scheduling Implementado

## 1. Alcance

Este documento cubre solo lo ya implementado en backend dentro de `scheduling` y listo para consumo frontend.

No cubre:

- `auth`
- `refresh`
- `/profile`
- alta de staff
- contratos futuros de citas completas
- EMR
- laboratorio

La base de `identity` y segmentación de rutas ya está en:

- `docs/frontend/01_AUTH_Y_RUTAS_NEXTJS.md`

## 2. Objetivo para frontend

Este archivo debe permitir que otro Codex o un frontend engineer integre sin interpretar el backend.

Incluye:

- endpoints reales
- roles autorizados
- query params
- payloads exactos
- respuestas exactas
- `curl` listos para copiar
- reglas de consumo importantes para UI

## 3. Base común

Base URL local:

```text
http://localhost:8080/api/v1
```

Header de autenticación:

```text
Authorization: Bearer TU_ACCESS_TOKEN
```

Envelope común:

```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Errores esperados:

- `400` para validación o regla de negocio
- `401` si el token es inválido o expiró
- `403` si el rol no tiene permiso
- `404` si el recurso no existe

Formato típico de error de validación:

```json
{
  "success": false,
  "message": "Error de validación",
  "data": {
    "fieldName": "mensaje de validación"
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Formato típico de error de negocio:

```json
{
  "success": false,
  "message": "La fecha final no puede ser anterior a la fecha inicial",
  "timestamp": "2026-05-10T12:00:00Z"
}
```

## 4. Rutas frontend sugeridas

Doctor:

- `/doctor/schedule/profile`
- `/doctor/schedule/availability`
- `/doctor/schedule/settings`
- `/doctor/schedule/readiness`
- `/doctor/schedule/blocks`

Admin:

- `/admin/calendar/holidays`

Doctor o Admin:

- `/doctor/calendar/holidays`
- `/admin/calendar/holidays`

Student:

- `/patient/appointments/search`

## 5. Endpoints implementados

| Método | Endpoint | Rol |
|---|---|---|
| `GET` | `/doctors/me/availability` | `DOCTOR` |
| `PUT` | `/doctors/me/availability` | `DOCTOR` |
| `GET` | `/doctors/me/schedule-settings` | `DOCTOR` |
| `PUT` | `/doctors/me/schedule-settings` | `DOCTOR` |
| `GET` | `/doctors/me/schedule-readiness` | `DOCTOR` |
| `GET` | `/doctors/me/blocks` | `DOCTOR` |
| `POST` | `/doctors/me/blocks` | `DOCTOR` |
| `PUT` | `/doctors/me/blocks/{blockId}` | `DOCTOR` |
| `DELETE` | `/doctors/me/blocks/{blockId}` | `DOCTOR` |
| `GET` | `/calendar/holidays` | `ADMIN`, `DOCTOR` |
| `POST` | `/calendar/holidays` | `ADMIN` |
| `PUT` | `/calendar/holidays/{holidayId}` | `ADMIN` |
| `DELETE` | `/calendar/holidays/{holidayId}` | `ADMIN` |
| `GET` | `/appointments/slots` | `STUDENT`, `DOCTOR`, `ADMIN` |

## 6. Disponibilidad semanal del médico

### 6.1 GET /doctors/me/availability

Uso:

- recuperar todas las franjas semanales actuales del médico autenticado

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/availability' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "doctorUserId": "DOCTOR_USER_ID",
    "weeklyAvailability": [
      {
        "id": "SLOT_ID",
        "dayOfWeek": "MONDAY",
        "startTime": "08:00:00",
        "endTime": "12:00:00"
      }
    ]
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Notas frontend:

- `weeklyAvailability` puede venir vacío
- `dayOfWeek` usa enum Java: `MONDAY` a `SUNDAY`
- las horas llegan como `HH:mm:ss`

### 6.2 PUT /doctors/me/availability

Uso:

- reemplaza toda la configuración semanal actual

Payload exacto:

```json
{
  "weeklyAvailability": [
    {
      "dayOfWeek": "MONDAY",
      "startTime": "08:00",
      "endTime": "12:00"
    },
    {
      "dayOfWeek": "WEDNESDAY",
      "startTime": "14:00",
      "endTime": "18:00"
    }
  ]
}
```

`curl` exacto:

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/doctors/me/availability' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "weeklyAvailability": [
    {
      "dayOfWeek": "MONDAY",
      "startTime": "08:00",
      "endTime": "12:00"
    },
    {
      "dayOfWeek": "WEDNESDAY",
      "startTime": "14:00",
      "endTime": "18:00"
    }
  ]
}'
```

Respuesta:

```json
{
  "success": true,
  "message": "Disponibilidad semanal actualizada",
  "data": {
    "doctorUserId": "DOCTOR_USER_ID",
    "weeklyAvailability": [
      {
        "id": "SLOT_ID_1",
        "dayOfWeek": "MONDAY",
        "startTime": "08:00:00",
        "endTime": "12:00:00"
      },
      {
        "id": "SLOT_ID_2",
        "dayOfWeek": "WEDNESDAY",
        "startTime": "14:00:00",
        "endTime": "18:00:00"
      }
    ]
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Validaciones relevantes:

- `weeklyAvailability` es obligatorio
- cada slot requiere `dayOfWeek`, `startTime`, `endTime`
- no se permiten franjas solapadas en el mismo día
- `endTime` debe ser posterior a `startTime`

## 7. Parámetros de agenda

### 7.1 GET /doctors/me/schedule-settings

Uso:

- recuperar duración de consulta del médico

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/schedule-settings' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "doctorUserId": "DOCTOR_USER_ID",
    "appointmentDurationMinutes": 20,
    "configured": false
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Notas frontend:

- si nunca se configuró, el backend responde `appointmentDurationMinutes: 20`
- en ese caso `configured` viene en `false`

### 7.2 PUT /doctors/me/schedule-settings

Payload exacto:

```json
{
  "appointmentDurationMinutes": 20
}
```

`curl` exacto:

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/doctors/me/schedule-settings' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "appointmentDurationMinutes": 20
}'
```

Respuesta:

```json
{
  "success": true,
  "message": "Parámetros de agenda actualizados",
  "data": {
    "doctorUserId": "DOCTOR_USER_ID",
    "appointmentDurationMinutes": 20,
    "configured": true
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Validaciones relevantes:

- mínimo `10`
- máximo `120`

## 8. Readiness de agenda

### 8.1 GET /doctors/me/schedule-readiness

Uso:

- saber si la agenda ya puede publicar slots

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/schedule-readiness' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "doctorUserId": "DOCTOR_USER_ID",
    "profileComplete": true,
    "weeklyAvailabilityConfigured": true,
    "scheduleSettingsConfigured": true,
    "readyForPublishing": true,
    "missingRequirements": []
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Valores posibles en `missingRequirements`:

- `PROFILE_PROFESSIONAL_INCOMPLETE`
- `WEEKLY_AVAILABILITY_NOT_CONFIGURED`
- `SCHEDULE_SETTINGS_NOT_CONFIGURED`

Notas frontend:

- este endpoint debe usarse para mostrar alertas y estado de agenda
- no inferir readiness solo desde formularios locales

## 9. Bloqueos puntuales del médico

### 9.1 GET /doctors/me/blocks

Uso:

- listar todos los bloqueos del médico
- o filtrar por rango

Sin filtros:

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/blocks' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

Con filtros:

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/blocks?dateFrom=2026-05-01&dateTo=2026-05-31' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": [
    {
      "id": "BLOCK_ID",
      "doctorUserId": "DOCTOR_USER_ID",
      "date": "2026-05-29",
      "startTime": "10:00:00",
      "endTime": "12:00:00",
      "allDay": false,
      "reason": "Licencia médica"
    }
  ],
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Reglas importantes:

- enviar ambas fechas o ninguna
- si `dateTo < dateFrom`, responde `400`

### 9.2 POST /doctors/me/blocks

Bloqueo parcial:

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/blocks' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "date": "2026-05-29",
  "startTime": "10:00",
  "endTime": "12:00",
  "reason": "Licencia médica"
}'
```

Bloqueo de día completo:

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/blocks' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "date": "2026-05-30",
  "reason": "Congreso"
}'
```

Respuesta típica:

```json
{
  "success": true,
  "message": "Bloqueo puntual registrado",
  "data": {
    "id": "BLOCK_ID",
    "doctorUserId": "DOCTOR_USER_ID",
    "date": "2026-05-29",
    "startTime": "10:00:00",
    "endTime": "12:00:00",
    "allDay": false,
    "reason": "Licencia médica"
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Validaciones y reglas:

- `date` es obligatoria
- `reason` es obligatoria
- si se manda una hora, se deben mandar ambas
- `endTime` debe ser posterior a `startTime`
- no se permiten solapamientos con otro bloqueo del mismo médico
- no se permite crear bloqueo si coincide con citas futuras activas

### 9.3 PUT /doctors/me/blocks/{blockId}

`curl` exacto:

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/doctors/me/blocks/BLOCK_ID' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "date": "2026-05-29",
  "startTime": "11:00",
  "endTime": "13:00",
  "reason": "Licencia médica ajustada"
}'
```

Respuesta:

```json
{
  "success": true,
  "message": "Bloqueo puntual actualizado",
  "data": {
    "id": "BLOCK_ID",
    "doctorUserId": "DOCTOR_USER_ID",
    "date": "2026-05-29",
    "startTime": "11:00:00",
    "endTime": "13:00:00",
    "allDay": false,
    "reason": "Licencia médica ajustada"
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

### 9.4 DELETE /doctors/me/blocks/{blockId}

`curl` exacto:

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/doctors/me/blocks/BLOCK_ID' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "Bloqueo puntual eliminado",
  "data": null,
  "timestamp": "2026-05-10T12:00:00Z"
}
```

## 10. Calendario institucional

### 10.1 GET /calendar/holidays

Uso:

- consulta de restricciones institucionales
- disponible para `ADMIN` y `DOCTOR`

Sin filtros:

```bash
curl --location 'http://localhost:8080/api/v1/calendar/holidays' \
--header 'Authorization: Bearer TU_DOCTOR_O_ADMIN_ACCESS_TOKEN'
```

Con filtros:

```bash
curl --location 'http://localhost:8080/api/v1/calendar/holidays?dateFrom=2026-05-01&dateTo=2026-05-31' \
--header 'Authorization: Bearer TU_DOCTOR_O_ADMIN_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": [
    {
      "id": "HOLIDAY_ID",
      "date": "2026-05-27",
      "type": "PARTIAL",
      "startTime": "08:00:00",
      "endTime": "12:00:00",
      "reason": "Jornada universitaria"
    }
  ],
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Reglas importantes:

- enviar ambas fechas o ninguna
- si `dateTo < dateFrom`, responde `400`
- `type` puede ser `TOTAL` o `PARTIAL`

### 10.2 POST /calendar/holidays

Jornada parcial:

```bash
curl --location 'http://localhost:8080/api/v1/calendar/holidays' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "date": "2026-05-27",
  "type": "PARTIAL",
  "startTime": "08:00",
  "endTime": "12:00",
  "reason": "Jornada universitaria"
}'
```

Feriado total:

```bash
curl --location 'http://localhost:8080/api/v1/calendar/holidays' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "date": "2026-05-28",
  "type": "TOTAL",
  "reason": "Suspensión de actividades"
}'
```

Respuesta típica:

```json
{
  "success": true,
  "message": "Restricción institucional registrada",
  "data": {
    "id": "HOLIDAY_ID",
    "date": "2026-05-27",
    "type": "PARTIAL",
    "startTime": "08:00:00",
    "endTime": "12:00:00",
    "reason": "Jornada universitaria"
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Validaciones y reglas:

- `date`, `type`, `reason` son obligatorios
- `TOTAL` no debe enviar `startTime` ni `endTime`
- `PARTIAL` requiere `startTime` y `endTime`
- `endTime` debe ser posterior a `startTime`
- no se permite más de una restricción incompatible para la misma fecha

### 10.3 PUT /calendar/holidays/{holidayId}

`curl` exacto:

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/calendar/holidays/HOLIDAY_ID' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "date": "2026-05-27",
  "type": "TOTAL",
  "reason": "Suspensión de actividades"
}'
```

Respuesta:

```json
{
  "success": true,
  "message": "Restricción institucional actualizada",
  "data": {
    "id": "HOLIDAY_ID",
    "date": "2026-05-27",
    "type": "TOTAL",
    "startTime": null,
    "endTime": null,
    "reason": "Suspensión de actividades"
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

### 10.4 DELETE /calendar/holidays/{holidayId}

`curl` exacto:

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/calendar/holidays/HOLIDAY_ID' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "Restricción institucional eliminada",
  "data": null,
  "timestamp": "2026-05-10T12:00:00Z"
}
```

## 11. Consulta de slots disponibles

### 11.1 GET /appointments/slots

Uso:

- buscar slots disponibles para un médico y una fecha
- disponible para `STUDENT`, `DOCTOR`, `ADMIN`

Query params exactos:

- `doctorId`: `UUID` del `users.id` del médico
- `date`: fecha ISO `YYYY-MM-DD`

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/appointments/slots?doctorId=DOCTOR_ID&date=2026-05-25' \
--header 'Authorization: Bearer TU_STUDENT_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "doctorId": "DOCTOR_ID",
    "date": "2026-05-25",
    "appointmentDurationMinutes": 20,
    "readyForPublishing": true,
    "slots": [
      {
        "startAt": "2026-05-25T08:00:00",
        "endAt": "2026-05-25T08:20:00"
      }
    ]
  },
  "timestamp": "2026-05-10T12:00:00Z"
}
```

Reglas de negocio importantes para frontend:

- si la agenda no está lista, responde `200` con `readyForPublishing: false` y `slots: []`
- si no hay disponibilidad semanal para ese día, responde `200` con `slots: []`
- si existe feriado total, responde `200` con `slots: []`
- excluye jornadas parciales, bloqueos y citas activas
- el frontend no debe recalcular slots; debe confiar en el backend

## 12. Reglas de integración

- tratar todos estos endpoints como contratos reales ya implementados
- no asumir que `configured: false` o `readyForPublishing: false` son errores; son estados válidos de UI
- usar `missingRequirements` para mensajes de onboarding o alertas
- para formularios de bloques y feriados, propagar mensajes de negocio del backend tal como llegan
- no exponer pantalla ni acción si el rol no corresponde, aunque el backend igualmente protege

## 13. Lo que sigue pendiente

Estos contratos todavía no existen y no deben integrarse como reales:

- `POST /appointments`
- `DELETE /appointments/{id}`
- `GET /appointments/week`
- `PATCH /appointments/{id}/reschedule`
- `PATCH /appointments/{id}/status`

Para esas piezas, la fuente vigente sigue siendo `docs/endpoints/01_API_REST_ACTUAL.md` como contrato objetivo, no como implementación existente.
