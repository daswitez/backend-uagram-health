# API REST y Endpoints del Sistema

Este documento lista los endpoints REST expuestos por el sistema, sus funciones, los roles requeridos para acceder a ellos, y **ejemplos en cURL listos para importar a Postman** o probar en terminal.

> **Base URL:** `http://localhost:8080/api/v1`

Todos los endpoints (excepto `/auth/login` y archivos estáticos) requieren enviar un header de autorización:
`Authorization: Bearer <access_token>`

## Formato de Respuesta Universal

El frontend (React Native o Next.js) siempre recibirá este formato de "envelope":

```json
{
  "success": true, 
  "message": "OK",
  "data": { ... },
  "timestamp": "2026-04-28T22:00:00Z"
}
```

---

## 1. Módulo: Identity (Autenticación y Usuarios)

| Método | Endpoint | Rol Requerido | Descripción |
|---|---|---|---|
| `POST` | `/auth/register` | *Público* | Registra a un nuevo estudiante. Requiere R.U., email, contraseña, nombre y apellidos. |
| `POST` | `/auth/login` | *Público* | Inicia sesión usando Email (médicos) o R.U. (estudiantes). Retorna JWT Access (15m) y Refresh (7d). |
| `POST` | `/auth/refresh` | *Público* | Renueva el Access Token usando un Refresh Token válido. |
| `GET` | `/profile` | *Autenticado* | Retorna los datos del perfil del usuario actualmente autenticado (calculado desde el token). |

### Ejemplos cURL (Postman / Frontend)

**1. Registro de Estudiante**
```bash
curl --location 'http://localhost:8080/api/v1/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "ru": "220056789",
    "email": "nuevo.estudiante@est.uagrm.edu.bo",
    "password": "miPassword123",
    "firstName": "Ana",
    "lastName": "Gómez",
    "phone": "+591 70000005",
    "career": "Arquitectura",
    "bloodType": "A+"
}'
```

**2. Login como Estudiante (Usando R.U.)**
```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "identifier": "220012345",
    "password": "student123"
}'
```

**2. Login como Médico / Admin (Usando Email)**
```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "identifier": "dr.martinez@uagrm.edu.bo",
    "password": "doctor123"
}'
```

**3. Obtener Perfil de Usuario**
```bash
curl --location 'http://localhost:8080/api/v1/profile' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN'
```

**4. Renovar Token (Refresh)**
```bash
curl --location 'http://localhost:8080/api/v1/auth/refresh' \
--header 'Content-Type: application/json' \
--data '{
    "refreshToken": "AQUI_TU_REFRESH_TOKEN"
}'
```

---

## 2. Módulo: Scheduling (Agendamiento) - *[En Desarrollo]*

| Método | Endpoint | Rol Requerido | Descripción |
|---|---|---|---|
| `GET` | `/appointments/slots` | `STUDENT` | Devuelve los bloques de tiempo libres para un médico/especialidad. |
| `POST` | `/appointments` | `STUDENT` | El estudiante crea (reserva) una nueva cita. Valida que no haya conflicto. |
| `DELETE` | `/appointments/{id}`| `STUDENT` | El estudiante cancela su propia cita (requiere antelación mínima). |
| `GET` | `/appointments/week` | `DOCTOR` | Datos para el Kanban: todas las citas del médico en una semana. |
| `PATCH` | `/appointments/{id}/reschedule` | `DOCTOR` | Acción Drag&Drop del Kanban para mover una cita de horario. |

### Ejemplos cURL (Postman / Frontend)

**1. Ver horarios disponibles (Slots)**
```bash
curl --location 'http://localhost:8080/api/v1/appointments/slots?doctorId=a0000000-0000-0000-0000-000000000002&date=2026-05-01' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN'
```

**2. Agendar Nueva Cita (Estudiante)**
```bash
curl --location 'http://localhost:8080/api/v1/appointments' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN' \
--data '{
    "doctorId": "a0000000-0000-0000-0000-000000000002",
    "scheduledStart": "2026-05-01T10:00:00Z",
    "type": "ROUTINE"
}'
```

**3. Obtener Citas de la Semana (Kanban del Médico)**
```bash
curl --location 'http://localhost:8080/api/v1/appointments/week?startDate=2026-04-27' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN'
```

---

## 3. Módulo: EMR (Historia Clínica) - *[En Desarrollo]*

| Método | Endpoint | Rol Requerido | Descripción |
|---|---|---|---|
| `GET` | `/emr/{patientId}` | `DOCTOR` | Obtiene el historial clínico cronológico completo del paciente. |
| `POST` | `/emr/records` | `DOCTOR` | Guarda una nueva ficha clínica (cifra el texto y genera Hash Blockchain). |
| `GET` | `/emr/snippets` | `DOCTOR` | Lista los atajos de teclado o macros (ej. `/gripe`) activos. |
| `PUT` | `/emr/drafts/{id}` | `DOCTOR` | Autoguardado silencioso para evitar pérdida de datos. |

### Ejemplos cURL (Postman / Frontend)

**1. Obtener Historial del Paciente**
```bash
curl --location 'http://localhost:8080/api/v1/emr/a0000000-0000-0000-0000-000000000003' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN'
```

**2. Guardar Ficha Clínica (Inmutable)**
```bash
curl --location 'http://localhost:8080/api/v1/emr/records' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN' \
--data '{
    "patientId": "a0000000-0000-0000-0000-000000000003",
    "appointmentId": "UUID_DE_LA_CITA",
    "clinicalContent": "Paciente presenta cuadro febril de 3 días de evolución..."
}'
```

**3. Consultar Snippets (Macros)**
```bash
curl --location 'http://localhost:8080/api/v1/emr/snippets' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN'
```

---

## 4. Módulo: Laboratorio (Órdenes y Archivos) - *[En Desarrollo]*

| Método | Endpoint | Rol Requerido | Descripción |
|---|---|---|---|
| `POST` | `/lab/orders` | `DOCTOR` | El médico solicita pruebas de laboratorio al finalizar una consulta. |
| `GET` | `/lab/orders` | `DOCTOR`, `LAB_TECH`| Lista las órdenes pendientes. Prioriza por nivel de urgencia (`ROUTINE` vs `URGENT`). |
| `POST` | `/lab/orders/batch-approve` | `LAB_TECH` | Aprueba masivamente (Batch Approval) múltiples resultados normales simultáneamente. |
| `POST` | `/lab/upload` | `LAB_TECH` | Endpoint para Dropzone (Sube PDF o DICOM a MinIO y actualiza la orden). |
| `GET` | `/lab/results/{id}/pdf` | `DOCTOR`, `STUDENT`| Genera o devuelve el PDF final de los resultados con firma válida. |

### Ejemplos cURL (Postman / Frontend)

**1. Crear Orden de Laboratorio (Médico)**
```bash
curl --location 'http://localhost:8080/api/v1/lab/orders' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN' \
--data '{
    "patientId": "a0000000-0000-0000-0000-000000000003",
    "priority": "ROUTINE",
    "catalogItemIds": ["UUID_CATALOGO_1", "UUID_CATALOGO_2"],
    "clinicalNotes": "Evaluar posible anemia."
}'
```

**2. Aprobación por Lotes (Laboratorista)**
```bash
curl --location 'http://localhost:8080/api/v1/lab/orders/batch-approve' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN' \
--data '{
    "orderIds": ["UUID_ORDEN_1", "UUID_ORDEN_2", "UUID_ORDEN_3"]
}'
```

**3. Subir PDF a la Orden (Dropzone)**
```bash
curl --location 'http://localhost:8080/api/v1/lab/upload' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN' \
--form 'orderItemId="UUID_DEL_ITEM"' \
--form 'file=@"/ruta/local/al/resultado.pdf"'
```

---

## 5. Otros (Recetas)

| Método | Endpoint | Rol Requerido | Descripción |
|---|---|---|---|
| `GET` | `/prescriptions/active` | `STUDENT` | Lista de medicamentos recetados vigentes para el estudiante. |

### Ejemplos cURL (Postman / Frontend)

**1. Obtener Recetas Activas**
```bash
curl --location 'http://localhost:8080/api/v1/prescriptions/active' \
--header 'Authorization: Bearer AQUI_TU_ACCESS_TOKEN'
```
