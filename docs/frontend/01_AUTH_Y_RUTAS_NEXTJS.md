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

## 5. Brechas actuales que impactan al frontend

Aunque la navegación ya exista, el backend todavía no completa:

- agenda médica configurable
- slots reales para reservas
- EMR operativo
- laboratorio operativo

Por eso el frontend puede tener vistas listas, pero varias todavía dependen de contratos no implementados.

## 6. Reglas de compatibilidad

Cuando se agregue o cambie un endpoint backend:

1. mantener `ApiResponse`
2. no romper nombres de rol ya usados en frontend
3. actualizar `docs/endpoints`
4. validar impacto en permisos y need to know
