# Frontend Next.js, Rutas y Contratos por Rol

## 1. Estado actual

Según el estado vigente del proyecto, el frontend ya está integrado con:

- rutas por rol
- layouts diferenciados
- flujo de autenticación conectado al backend

Este documento deja claros los contratos que el backend debe respetar para no romper esa integración.

## 2. Regla principal

La separación de rutas frontend mejora UX y orden, pero la seguridad efectiva sigue dependiendo del backend.

Siempre aplicar:

- mínimo privilegio
- need to know

## 3. Segmentación funcional por rol

### 3.1 Admin

Rutas esperadas:

- `/admin/...`

Responsabilidades:

- alta de staff
- gestión operativa
- catálogos
- calendario institucional

No debe ver:

- contenido clínico
- EMR
- diagnósticos

### 3.2 Doctor

Rutas esperadas:

- `/doctor/...`

Responsabilidades:

- agenda propia
- configuración de disponibilidad
- bloqueos y excepciones
- atención clínica
- recetas
- órdenes de laboratorio

### 3.3 Student

Rutas esperadas:

- `/patient/...`

Responsabilidades:

- ver perfil propio
- ver citas propias
- reservar y cancelar citas propias
- ver recetas y resultados propios

### 3.4 Lab Tech

Rutas esperadas:

- `/lab/...` o equivalente segregado por rol

Responsabilidades:

- worklist de órdenes
- carga de archivos
- aprobación y publicación de resultados

## 4. Contratos backend que hoy sostienen la integración

Endpoints activos:

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `GET /api/v1/profile`
- `POST /api/v1/admin/users/staff`
- `GET /api/v1/doctors/me/profile`
- `PUT /api/v1/doctors/me/profile`
- `GET /api/v1/doctors/me/availability`
- `PUT /api/v1/doctors/me/availability`
- `GET /api/v1/doctors/me/schedule-settings`
- `PUT /api/v1/doctors/me/schedule-settings`
- `GET /api/v1/doctors/me/schedule-readiness`
- `GET /api/v1/doctors/me/blocks`
- `POST /api/v1/doctors/me/blocks`
- `PUT /api/v1/doctors/me/blocks/{id}`
- `DELETE /api/v1/doctors/me/blocks/{id}`
- `GET /api/v1/calendar/holidays`
- `POST /api/v1/calendar/holidays`
- `PUT /api/v1/calendar/holidays/{id}`
- `DELETE /api/v1/calendar/holidays/{id}`
- `GET /api/v1/appointments/slots`

## 5. Brechas actuales que impactan al frontend

Aunque la navegación ya exista, el backend todavía no completa:

- reserva y cancelación de citas
- agenda semanal operativa del médico
- reprogramación y cambio de estado de citas
- EMR operativo
- laboratorio operativo

La configuración de agenda médica, restricciones institucionales, bloqueos y consulta de slots ya tienen soporte backend básico. Las pantallas que dependan de operación completa de citas, EMR y laboratorio todavía deben tratar sus endpoints como contratos objetivo hasta que exista controlador real.

## 6. Reglas de compatibilidad

Cuando se agregue o cambie un endpoint backend:

1. mantener `ApiResponse`
2. no romper nombres de rol ya usados en frontend
3. actualizar `docs/endpoints`
4. validar impacto en permisos y need to know
5. si una historia transversal se divide en tareas Jira, mantener estable el contrato público o documentar la transición
