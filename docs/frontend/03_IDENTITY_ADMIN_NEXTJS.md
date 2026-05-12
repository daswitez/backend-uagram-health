# Frontend Next.js - Identity Admin y Login Enriquecido

## 1. Alcance

Este documento cubre lo nuevo y vigente en `identity` para consumo frontend:

- `POST /auth/login` con `userType`, `firstName`, `lastName` y `specialty` en `data`
- `POST /auth/refresh` con el mismo contrato enriquecido
- alta de staff por `ADMIN`
- listado de cuentas de staff
- detalle de cuenta de staff
- edición de cuenta de staff

No cubre:

- auto-registro de estudiante
- módulos de scheduling
- EMR
- laboratorio

Las bases generales de rutas por rol están en:

- `docs/frontend/01_AUTH_Y_RUTAS_NEXTJS.md`

## 2. Objetivo para frontend

Este archivo debe permitir que otro Codex o frontend engineer integre:

- login sin llamada extra obligatoria a `/profile`
- ruteo inmediato por `userType`
- panel admin de cuentas de staff
- formularios de soporte limitados a manejo de cuenta

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
  "timestamp": "2026-05-11T12:00:00Z"
}
```

Errores esperados:

- `400` para validación o regla de negocio
- `401` para credenciales inválidas o token vencido
- `403` para rol no autorizado
- `404` si la cuenta staff no existe

Error de validación típico:

```json
{
  "success": false,
  "message": "Error de validación",
  "data": {
    "email": "Debe ser un correo válido"
  },
  "timestamp": "2026-05-11T12:00:00Z"
}
```

Error de negocio típico:

```json
{
  "success": false,
  "message": "El correo electrónico ya está registrado",
  "timestamp": "2026-05-11T12:00:00Z"
}
```

## 4. Rutas frontend sugeridas

Admin:

- `/admin/staff`
- `/admin/staff/new`
- `/admin/staff/[userId]`
- `/admin/staff/[userId]/edit`

Doctor:

- `/doctor/...`

Student:

- `/patient/...`

Lab Tech:

- `/lab/...`

## 5. Login enriquecido

### 5.1 POST /auth/login

Uso:

- autenticar usuario
- obtener tokens
- obtener `userType` para decidir la ruta inicial del frontend
- obtener datos básicos para header, saludo y sesión

Payload exacto:

```json
{
  "identifier": "admin@uagrm.edu.bo",
  "password": "Test_123!"
}
```

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "identifier": "admin@uagrm.edu.bo",
  "password": "Test_123!"
}'
```

Respuesta:

```json
{
  "success": true,
  "message": "Inicio de sesión exitoso",
  "data": {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userType": "ADMIN",
    "firstName": "Administrador",
    "lastName": "FUSUM",
    "specialty": null,
    "user": {
      "id": "USER_ID",
      "email": "admin@uagrm.edu.bo",
      "ci": "12345678",
      "firstName": "Administrador",
      "lastName": "FUSUM",
      "fullName": "Administrador FUSUM",
      "phone": "+59170000000",
      "userType": "ADMIN",
      "active": true
    }
  },
  "timestamp": "2026-05-11T12:00:00Z"
}
```

Valores esperados para `userType`:

- `ADMIN`
- `DOCTOR`
- `STUDENT`
- `LAB_TECH`
- `RECEPTIONIST`

Reglas frontend:

- usar `userType` de `data` para rutear sin pegar a `/profile`
- `specialty` solo tiene valor para `DOCTOR`
- para roles no médicos, `specialty` viene en `null`
- el objeto `user` sigue existiendo por compatibilidad

### 5.2 POST /auth/refresh

Uso:

- renovar sesión sin volver a pedir `/profile`

Payload exacto:

```json
{
  "refreshToken": "TU_REFRESH_TOKEN"
}
```

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/auth/refresh' \
--header 'Content-Type: application/json' \
--data '{
  "refreshToken": "TU_REFRESH_TOKEN"
}'
```

Respuesta:

```json
{
  "success": true,
  "message": "Token renovado",
  "data": {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userType": "DOCTOR",
    "firstName": "Carlos",
    "lastName": "Martinez",
    "specialty": "Medicina Familiar",
    "user": {
      "id": "USER_ID",
      "email": "dr.martinez@uagrm.edu.bo",
      "ci": "12345678",
      "firstName": "Carlos",
      "lastName": "Martinez",
      "fullName": "Carlos Martinez",
      "phone": "+59170000002",
      "userType": "DOCTOR",
      "active": true
    }
  },
  "timestamp": "2026-05-11T12:00:00Z"
}
```

## 6. Alta de staff por admin

### 6.1 POST /admin/users/staff

Rol:

- `ADMIN`

Uso:

- crear cuentas de `DOCTOR`, `LAB_TECH`, `ADMIN` o `RECEPTIONIST`
- no sirve para `STUDENT` ni `PATIENT`

Payload exacto:

```json
{
  "ci": "87654321",
  "email": "nuevo.doctor@uagrm.edu.bo",
  "firstName": "Carlos",
  "lastName": "Vargas",
  "phone": "+59170000001",
  "userType": "DOCTOR",
  "medicalLicense": "MP-9988",
  "specialty": "Cardiología"
}
```

`curl` exacto:

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

Respuesta:

```json
{
  "success": true,
  "message": "Personal registrado con éxito. Correo de bienvenida enviado.",
  "data": {
    "email": "nuevo.doctor@uagrm.edu.bo",
    "role": "DOCTOR",
    "tempPassword": "Ab1!Cd2#Ef3$",
    "emailSent": "true"
  },
  "timestamp": "2026-05-11T12:00:00Z"
}
```

Reglas importantes:

- para `DOCTOR`, `medicalLicense` y `specialty` son obligatorios
- no usar este endpoint para estudiantes/pacientes
- `tempPassword` vuelve en la respuesta como fallback operativo

## 7. Listado de cuentas de staff

### 7.1 GET /admin/users/staff

Rol:

- `ADMIN`

Uso:

- obtener todas las cuentas staff
- incluye `ADMIN`, `DOCTOR`, `LAB_TECH`, `RECEPTIONIST`
- excluye `STUDENT` y `PATIENT`

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/admin/users/staff' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": [
    {
      "id": "USER_ID",
      "email": "nuevo.doctor@uagrm.edu.bo",
      "ci": "87654321",
      "firstName": "Carlos",
      "lastName": "Vargas",
      "fullName": "Carlos Vargas",
      "phone": "+59170000001",
      "userType": "DOCTOR",
      "active": true,
      "specialty": "Cardiología",
      "medicalLicense": "MP-9988",
      "createdAt": "2026-05-11T15:00:00Z",
      "updatedAt": "2026-05-11T15:00:00Z"
    },
    {
      "id": "USER_ID_2",
      "email": "lab.garcia@uagrm.edu.bo",
      "ci": "99887766",
      "firstName": "Lucia",
      "lastName": "Garcia",
      "fullName": "Lucia Garcia",
      "phone": "+59170000003",
      "userType": "LAB_TECH",
      "active": true,
      "specialty": null,
      "medicalLicense": null,
      "createdAt": "2026-05-11T15:00:00Z",
      "updatedAt": "2026-05-11T15:00:00Z"
    }
  ],
  "timestamp": "2026-05-11T12:00:00Z"
}
```

Notas frontend:

- `specialty` y `medicalLicense` solo aplican a `DOCTOR`
- `active` sirve para badges y filtros
- ideal para tabla administrativa, no para vista clínica

## 8. Detalle de cuenta de staff

### 8.1 GET /admin/users/staff/{userId}

Rol:

- `ADMIN`

Uso:

- obtener detalle de una cuenta staff específica

`curl` exacto:

```bash
curl --location 'http://localhost:8080/api/v1/admin/users/staff/USER_ID' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN'
```

Respuesta:

```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": "USER_ID",
    "email": "nuevo.doctor@uagrm.edu.bo",
    "ci": "87654321",
    "firstName": "Carlos",
    "lastName": "Vargas",
    "fullName": "Carlos Vargas",
    "phone": "+59170000001",
    "userType": "DOCTOR",
    "active": true,
    "specialty": "Cardiología",
    "medicalLicense": "MP-9988",
    "createdAt": "2026-05-11T15:00:00Z",
    "updatedAt": "2026-05-11T15:30:00Z"
  },
  "timestamp": "2026-05-11T12:00:00Z"
}
```

## 9. Edición de cuenta de staff

### 9.1 PUT /admin/users/staff/{userId}

Rol:

- `ADMIN`

Uso:

- editar solo manejo de cuenta
- permite activar/desactivar cuenta
- para `DOCTOR`, también permite editar `specialty` y `medicalLicense`

Payload exacto para doctor:

```json
{
  "ci": "87654321",
  "email": "nuevo.doctor@uagrm.edu.bo",
  "firstName": "Carlos",
  "lastName": "Vargas",
  "phone": "+59170000009",
  "active": true,
  "medicalLicense": "MP-10001",
  "specialty": "Medicina Interna"
}
```

Payload exacto para staff no médico:

```json
{
  "ci": "99887766",
  "email": "lab.garcia@uagrm.edu.bo",
  "firstName": "Lucia",
  "lastName": "Garcia",
  "phone": "+59170000003",
  "active": false,
  "medicalLicense": null,
  "specialty": null
}
```

`curl` exacto:

```bash
curl --location --request PUT 'http://localhost:8080/api/v1/admin/users/staff/USER_ID' \
--header 'Authorization: Bearer TU_ADMIN_ACCESS_TOKEN' \
--header 'Content-Type: application/json' \
--data '{
  "ci": "87654321",
  "email": "nuevo.doctor@uagrm.edu.bo",
  "firstName": "Carlos",
  "lastName": "Vargas",
  "phone": "+59170000009",
  "active": true,
  "medicalLicense": "MP-10001",
  "specialty": "Medicina Interna"
}'
```

Respuesta:

```json
{
  "success": true,
  "message": "Cuenta de personal actualizada",
  "data": {
    "id": "USER_ID",
    "email": "nuevo.doctor@uagrm.edu.bo",
    "ci": "87654321",
    "firstName": "Carlos",
    "lastName": "Vargas",
    "fullName": "Carlos Vargas",
    "phone": "+59170000009",
    "userType": "DOCTOR",
    "active": true,
    "specialty": "Medicina Interna",
    "medicalLicense": "MP-10001",
    "createdAt": "2026-05-11T15:00:00Z",
    "updatedAt": "2026-05-11T16:00:00Z"
  },
  "timestamp": "2026-05-11T12:00:00Z"
}
```

Reglas importantes:

- no cambia `userType`
- no toca pacientes ni información clínica
- si la cuenta es `DOCTOR`, `specialty` y `medicalLicense` deben venir con valor
- se validan unicidad de `email`, `ci` y `medicalLicense`
- `active` es obligatorio

## 10. Qué puede editar admin y qué no

Permitido:

- correo
- C.I.
- nombre
- apellido
- teléfono
- estado activo de la cuenta
- especialidad y matrícula si es doctor

No permitido desde estos endpoints:

- rol de la cuenta
- contraseña
- datos de estudiantes/pacientes
- información clínica
- reservas, citas o EMR

## 11. Reglas de integración frontend

- usar `data.userType` del login para decidir la ruta inicial
- mantener el objeto `user` por compatibilidad mientras exista frontend viejo
- para panel admin, tratar `GET /admin/users/staff` como fuente principal de la tabla
- usar `GET /admin/users/staff/{userId}` para precargar formularios de edición
- si `userType !== "DOCTOR"`, ocultar o deshabilitar inputs de `specialty` y `medicalLicense`
- mostrar mensajes de negocio del backend directamente en el formulario

## 12. Estado real

Todo lo documentado aquí ya existe en backend y pasó validación de compilación/tests.
