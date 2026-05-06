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

### 3.1 Identity

| Método | Endpoint | Estado | Rol | Notas |
|---|---|---|---|---|
| `POST` | `/auth/register/patient` | implementado | público | crea `STUDENT` |
| `POST` | `/auth/login` | implementado | público | login por `email` o `ci` |
| `POST` | `/auth/refresh` | implementado | público | refresh de tokens |
| `GET` | `/profile` | implementado | autenticado | perfil base del usuario autenticado |
| `POST` | `/admin/users/staff` | implementado | `ADMIN` | alta de staff |

### 3.2 Ejemplos válidos hoy

#### Login de estudiante

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "identifier": "12345678",
  "password": "Test_123!"
}'
```

#### Login de médico

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "identifier": "dr.martinez@uagrm.edu.bo",
  "password": "Test_123!"
}'
```

#### Obtener perfil

```bash
curl --location 'http://localhost:8080/api/v1/profile' \
--header 'Authorization: Bearer TU_ACCESS_TOKEN'
```

#### Alta de staff

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

### 4.1 Scheduling

Dependencias previas:

- perfil médico editable
- horarios de atención por médico
- feriados institucionales
- bloqueos o excepciones del médico
- motor de slots

| Método | Endpoint | Rol | Objetivo |
|---|---|---|---|
| `GET` | `/doctors/me/profile` | `DOCTOR` | ver perfil profesional |
| `PUT` | `/doctors/me/profile` | `DOCTOR` | editar perfil profesional |
| `GET` | `/doctors/me/availability` | `DOCTOR` | ver agenda base semanal |
| `PUT` | `/doctors/me/availability` | `DOCTOR` | definir horario semanal |
| `POST` | `/doctors/me/blocks` | `DOCTOR` | bloquear día o rango puntual |
| `DELETE` | `/doctors/me/blocks/{id}` | `DOCTOR` | levantar bloqueo |
| `GET` | `/calendar/holidays` | `ADMIN`, `DOCTOR` | consultar feriados vigentes |
| `POST` | `/calendar/holidays` | `ADMIN` | crear feriado o jornada parcial |
| `GET` | `/appointments/slots` | `STUDENT` | consultar slots disponibles |
| `POST` | `/appointments` | `STUDENT` | reservar cita |
| `DELETE` | `/appointments/{id}` | `STUDENT` | cancelar cita propia |
| `GET` | `/appointments/week` | `DOCTOR` | agenda semanal |
| `PATCH` | `/appointments/{id}/reschedule` | `DOCTOR` | mover cita |
| `PATCH` | `/appointments/{id}/status` | `DOCTOR` | cambiar estado operativo |

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
