# API REST Actual y Contratos Objetivo

## 1. Alcance

Este documento separa explícitamente:

- lo que **ya existe** en backend
- lo que está **definido como contrato objetivo**

Base URL local:

```text
http://localhost:8080/api/v1
```

## 2. Formato de respuesta

Todos los endpoints deben responder con el envelope:

```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "timestamp": "2026-05-05T12:00:00Z"
}
```

## 3. Endpoints implementados

### 3.1 Flujo implementado hoy

| Método | Endpoint | Estado | Rol | Notas |
|---|---|---|---|---|
| `POST` | `/auth/register/patient` | implementado | público | crea `STUDENT` |
| `POST` | `/auth/login` | implementado | público | login por `email` o `ci` |
| `POST` | `/auth/refresh` | implementado | público | refresh de tokens |
| `GET` | `/profile` | implementado | autenticado | perfil base del usuario autenticado |
| `POST` | `/admin/users/staff` | implementado | `ADMIN` | alta de staff |
| `GET` | `/doctors/me/profile` | implementado | `DOCTOR` | ver perfil profesional propio |
| `PUT` | `/doctors/me/profile` | implementado | `DOCTOR` | editar perfil profesional propio |
| `GET` | `/doctors/me/availability` | implementado | `DOCTOR` | ver disponibilidad semanal propia |
| `PUT` | `/doctors/me/availability` | implementado | `DOCTOR` | definir disponibilidad semanal propia |
| `GET` | `/doctors/me/schedule-settings` | implementado | `DOCTOR` | ver parámetros de agenda propios |
| `PUT` | `/doctors/me/schedule-settings` | implementado | `DOCTOR` | definir duración de consulta propia |
| `GET` | `/doctors/me/schedule-readiness` | implementado | `DOCTOR` | verificar si la agenda ya está lista para publicar slots |
| `GET` | `/doctors/me/blocks` | implementado | `DOCTOR` | listar bloqueos puntuales propios |
| `POST` | `/doctors/me/blocks` | implementado | `DOCTOR` | bloquear día o rango puntual propio |
| `PUT` | `/doctors/me/blocks/{id}` | implementado | `DOCTOR` | editar bloqueo puntual propio |
| `DELETE` | `/doctors/me/blocks/{id}` | implementado | `DOCTOR` | eliminar bloqueo puntual propio |
| `GET` | `/calendar/holidays` | implementado | `ADMIN`, `DOCTOR` | consultar restricciones institucionales |
| `POST` | `/calendar/holidays` | implementado | `ADMIN` | registrar feriado total o jornada parcial |
| `PUT` | `/calendar/holidays/{id}` | implementado | `ADMIN` | editar feriado o jornada parcial |
| `DELETE` | `/calendar/holidays/{id}` | implementado | `ADMIN` | eliminar restricción institucional |

### 3.2 Curl listos para Postman

Nota:

- los `curl` de abajo usan URL completa para poder copiar y pegar directo
- si quieres variable en Postman, usa `{{BASE_URL}}`, no `$base_url`
- si quieres variable en shell, define `BASE_URL='http://localhost:8080/api/v1'` y usa `$BASE_URL`

#### Flujo 1. Login de doctor

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "identifier": "dr.martinez@uagrm.edu.bo",
  "password": "Test_123!"
}'
```

#### Flujo 2. Ver perfil base autenticado

```bash
curl --location 'http://localhost:8080/api/v1/profile' \
--header 'Authorization: Bearer TU_ACCESS_TOKEN'
```

#### Flujo 3. Ver perfil profesional del médico

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/profile' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

#### Flujo 4. Editar perfil profesional del médico

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/doctors/me/profile' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "firstName": "Carlos",
  "lastName": "Martinez",
  "phone": "+59170000002",
  "specialty": "Medicina Familiar",
  "medicalLicense": "MP-12345"
}'
```

#### Flujo 5. Ver disponibilidad semanal del médico

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/availability' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

#### Flujo 6. Configurar disponibilidad semanal del médico

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

#### Flujo 7. Ver parámetros de agenda del médico

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/schedule-settings' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

#### Flujo 8. Configurar parámetros de agenda del médico

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/doctors/me/schedule-settings' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "appointmentDurationMinutes": 20
}'
```

#### Flujo 9. Ver estado de preparación de agenda

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/schedule-readiness' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

#### Flujo 10. Consultar calendario institucional

```bash
curl --location 'http://localhost:8080/api/v1/calendar/holidays' \
--header 'Authorization: Bearer TU_DOCTOR_O_ADMIN_ACCESS_TOKEN'
```

#### Flujo 11. Registrar feriado o jornada parcial

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

#### Flujo 12. Editar feriado o jornada parcial

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

#### Flujo 13. Eliminar restricción institucional

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/calendar/holidays/HOLIDAY_ID' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN'
```

#### Flujo 14. Registrar bloqueo puntual del médico

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

#### Flujo 15. Registrar bloqueo de día completo

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/blocks' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "date": "2026-05-30",
  "reason": "Congreso"
}'
```

#### Flujo 16. Consultar bloqueos puntuales del médico

```bash
curl --location 'http://localhost:8080/api/v1/doctors/me/blocks?dateFrom=2026-05-01&dateTo=2026-05-31' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

#### Flujo 17. Editar bloqueo puntual del médico

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

#### Flujo 18. Eliminar bloqueo puntual del médico

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/doctors/me/blocks/BLOCK_ID' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

#### Flujo 19. Login de estudiante

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "identifier": "12345678",
  "password": "Test_123!"
}'
```

#### Flujo 20. Refresh token

```bash
curl --location 'http://localhost:8080/api/v1/auth/refresh' \
--header 'Content-Type: application/json' \
--data '{
  "refreshToken": "TU_REFRESH_TOKEN"
}'
```

#### Flujo 21. Auto-registro de estudiante

```bash
curl --location 'http://localhost:8080/api/v1/auth/register/patient' \
--header 'Content-Type: application/json' \
--data '{
  "ci": "99999999",
  "email": "nuevo.estudiante@est.uagrm.edu.bo",
  "password": "Test_123!",
  "firstName": "Ana",
  "lastName": "Suarez",
  "phone": "+59170000123",
  "career": "Ingeniería en Sistemas",
  "bloodType": "A+"
}'
```

#### Flujo 22. Login de admin

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "identifier": "admin@uagrm.edu.bo",
  "password": "Test_123!"
}'
```

#### Flujo 23. Alta de staff por admin

```bash
curl --location 'http://localhost:8080/api/v1/admin/users/staff' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "ci": "87654321",
  "email": "nuevo.doctor@uagrm.edu.bo",
  "firstName": "Carlos",
  "lastName": "Vargas",
  "phone": "+59170000001",
  "userType": "DOCTOR",
  "medicalLicense": "MP-9988",
  "specialty": "Cardiología"
}'
```

## 4. Contratos objetivo priorizados

Estos endpoints todavía no deben asumirse como implementados. Sirven como contrato de trabajo entre backend y frontend.

### 4.1 Scheduling y disponibilidad médica

Ordenado por flujo y dependencia real.

Dependencias previas:

- perfil médico editable
- disponibilidad semanal por médico
- parámetros operativos de agenda
- calendario institucional consultable y mantenible
- bloqueos o excepciones del médico
- motor de slots

| Método | Endpoint | Rol | Objetivo |
|---|---|---|---|
| `GET` | `/appointments/slots` | `STUDENT` | consultar slots disponibles |
| `POST` | `/appointments` | `STUDENT` | reservar cita |
| `DELETE` | `/appointments/{id}` | `STUDENT` | cancelar cita propia |
| `GET` | `/appointments/week` | `DOCTOR` | agenda semanal |
| `PATCH` | `/appointments/{id}/reschedule` | `DOCTOR` | mover cita |
| `PATCH` | `/appointments/{id}/status` | `DOCTOR` | cambiar estado operativo |

#### Plantillas curl del flujo objetivo

Estas llamadas sirven como contrato para preparar Postman. Hoy pueden responder `404` o `planned` hasta que cada endpoint exista.

##### Paso 1. Consultar slots disponibles

```bash
curl --location 'http://localhost:8080/api/v1/appointments/slots?doctorId=DOCTOR_ID&date=2026-05-30' \
--header 'Authorization: Bearer TU_STUDENT_ACCESS_TOKEN'
```

##### Paso 2. Reservar cita

```bash
curl --location 'http://localhost:8080/api/v1/appointments' \
--header 'Authorization: Bearer TU_STUDENT_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "doctorId": "DOCTOR_ID",
  "scheduledStart": "2026-05-30T10:00:00-04:00"
}'
```

##### Paso 3. Cancelar cita propia

```bash
curl --location --request DELETE 'http://localhost:8080/api/v1/appointments/APPOINTMENT_ID' \
--header 'Authorization: Bearer TU_STUDENT_ACCESS_TOKEN'
```

##### Paso 4. Ver agenda semanal del médico

```bash
curl --location 'http://localhost:8080/api/v1/appointments/week?weekStart=2026-05-25' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN'
```

##### Paso 5. Reprogramar cita

```bash
curl --location --request PATCH 'http://localhost:8080/api/v1/appointments/APPOINTMENT_ID/reschedule' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "scheduledStart": "2026-05-30T11:00:00-04:00"
}'
```

##### Paso 6. Cambiar estado operativo de cita

```bash
curl --location --request PATCH 'http://localhost:8080/api/v1/appointments/APPOINTMENT_ID/status' \
--header 'Authorization: Bearer TU_DOCTOR_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "status": "COMPLETED"
}'
```

### 4.2 EMR

| Método | Endpoint | Rol | Objetivo |
|---|---|---|---|
| `GET` | `/emr/patients/{patientId}/timeline` | `DOCTOR` | historial cronológico |
| `POST` | `/emr/records` | `DOCTOR` | crear ficha clínica inmutable |
| `POST` | `/emr/corrections` | `DOCTOR` | anexar corrección |
| `GET` | `/emr/snippets` | `DOCTOR` | listar snippets activos |
| `PUT` | `/emr/drafts/{id}` | `DOCTOR` | autoguardado |
| `POST` | `/prescriptions` | `DOCTOR` | emitir receta |
| `GET` | `/prescriptions/active` | `STUDENT` | recetas activas propias |

### 4.3 Laboratory

| Método | Endpoint | Rol | Objetivo |
|---|---|---|---|
| `GET` | `/lab/catalog` | `ADMIN`, `DOCTOR` | consultar catálogo |
| `POST` | `/lab/catalog` | `ADMIN` | alta de examen |
| `POST` | `/lab/orders` | `DOCTOR` | crear orden |
| `GET` | `/lab/orders` | `DOCTOR`, `LAB_TECH` | worklist filtrada |
| `POST` | `/lab/orders/batch-approve` | `LAB_TECH` | aprobación por lotes |
| `POST` | `/lab/upload` | `LAB_TECH` | subir archivo a MinIO |
| `GET` | `/lab/results/{id}/pdf` | `DOCTOR`, `STUDENT` | descargar reporte |

## 5. Regla de uso

Si un endpoint no tiene controlador real en `src/main/java`, debe tratarse como `planned`.
