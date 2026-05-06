# Frontend Next.js - Guía de Autenticación, Rutas y Roles (RBAC)

Este documento detalla la estrategia de implementación para el Frontend en **Next.js** (utilizando App Router), abarcando el flujo de inicio de sesión diferenciado, la protección de rutas mediante Middleware y la separación de vistas según los roles de IAM (Identity and Access Management).

---

## 1. Estrategia de Inicio de Sesión (Login)

El backend expone un endpoint unificado para el inicio de sesión: `POST /api/v1/auth/login`. Sin embargo, desde la perspectiva de la interfaz de usuario (UX), el identificador cambia según el tipo de usuario.

### Variaciones de UI para el Formulario de Login:
Dado que el payload espera `{ "identifier": "...", "password": "..." }`:

1.  **Staff Médico y Administrativo (Admin, Doctor, Laboratorista):**
    *   **Identificador:** Correo electrónico institucional (ej. `admin@uagrm.edu.bo`).
    *   **Validación Frontend:** El campo de texto debe validar un formato de email.
2.  **Usuario / Paciente (Estudiante):**
    *   **Identificador:** Carnet de Identidad (C.I.) (ej. `12345678`).
    *   **Validación Frontend:** El campo de texto debe validar que solo contenga números.

*💡 **Recomendación UX:** Puedes implementar un solo formulario de Login que detecte automáticamente si el usuario ingresó un correo (tiene `@`) o un C.I. (solo números) para validarlo antes de enviarlo al backend.*

---

## 2. Gestión de Tokens y Estado de Sesión

Al iniciar sesión exitosamente, el backend responde con un `accessToken` y un `refreshToken`.

*   **Almacenamiento Seguro:** En Next.js, se recomienda almacenar el `accessToken` y `refreshToken` en **Cookies HttpOnly** (desde los Server Actions o Route Handlers de Next.js) o al menos en Cookies seguras si se usa el lado del cliente. Esto facilita que el Middleware de Next.js pueda leer el token en cada solicitud de página.
*   **Decodificación del Rol:** El Frontend debe decodificar el payload del JWT (o llamar inmediatamente a `/api/v1/profile`) para extraer el `userType` o `role` (ej. `ADMIN`, `DOCTOR`, `STUDENT`) y guardarlo en el contexto global (como Zustand, Redux o Context API).

---

## 3. Arquitectura de Rutas y Middleware (Protección RBAC)

En Next.js App Router (`/app`), utilizaremos **Route Groups** para separar los layouts de cada rol y un **Middleware** para restringir los accesos.

### 3.1. Estructura de Carpetas Sugerida

```text
src/app/
├── (auth)/
│   ├── login/page.tsx           # Formulario unificado de login
│   └── register/page.tsx        # Registro público de estudiantes
├── (admin)/
│   └── admin/
│       ├── layout.tsx           # Navbar y Sidebar exclusivos de Admin
│       ├── dashboard/page.tsx
│       └── iam/page.tsx         # Gestión de personal (IAM)
├── (doctor)/
│   └── doctor/
│       ├── layout.tsx           # Navbar y Sidebar exclusivos del Médico
│       ├── kanban/page.tsx      # Agenda de citas
│       └── emr/page.tsx         # Historias clínicas
├── (patient)/
│   └── patient/
│       ├── layout.tsx           # Layout amigable para estudiantes
│       ├── dashboard/page.tsx
│       └── appointments/page.tsx # Reserva de citas
└── middleware.ts                # El guardián de las rutas
```

### 3.2. Lógica del Middleware (`middleware.ts`)

El Middleware interceptará todas las peticiones a rutas protegidas.
1.  Verifica si existe la cookie con el token. Si no existe, redirige a `/login`.
2.  Extrae el rol del usuario (del JWT).
3.  **Reglas de Acceso:**
    *   Si el rol es `STUDENT` e intenta entrar a `/admin/...` o `/doctor/...` ➡️ Redirige a `/patient/dashboard` o muestra vista 403 No Autorizado.
    *   Si el rol es `DOCTOR` e intenta entrar a `/admin/...` ➡️ Redirige a `/doctor/kanban`.
    *   Si el rol es `ADMIN` e intenta entrar a `/patient/...` ➡️ Redirige a `/admin/iam`.

---

## 4. Vistas y Permisos por Rol

### 4.1. El Administrador (Módulo IAM)
Cuando un administrador (rol `ADMIN`) inicia sesión, el sistema lo dirige automáticamente a su panel.
*   **Alcance:** El administrador **no** atiende pacientes ni ve historias clínicas. Su función es operativa y de sistema.
*   **Pantalla Principal (IAM - Identity & Access Management):**
    *   Debe existir una tabla para visualizar a todo el personal (Doctores, Laboratoristas).
    *   Debe existir un botón/modal para "Dar de Alta" a nuevo personal (Consume `POST /api/v1/admin/users/staff`).
    *   **Restricciones de UI:** El Sidebar del Admin solo debe tener accesos directos a "Gestión de Personal", "Configuraciones Globales", etc.

### 4.2. El Médico (Módulo EMR & Scheduling)
Cuando un médico (rol `DOCTOR`) inicia sesión, el contexto de la aplicación cambia totalmente.
*   **Alcance:** Solo gestiona la atención médica.
*   **Pantalla Principal:**
    *   Vista inicial: El **Kanban de su Agenda**, mostrando las citas del día.
    *   Vistas secundarias: Buscador de pacientes (para acceder al EMR) y módulo para crear órdenes de laboratorio.
    *   **Restricciones de UI:** No puede crear nuevos doctores ni ver el panel IAM del administrador.

### 4.3. El Paciente / Estudiante
Cuando el estudiante (rol `STUDENT`) inicia sesión, la UI es más cercana a un portal de servicios al cliente.
*   **Alcance:** Solo ve y gestiona sus propios datos.
*   **Pantalla Principal:**
    *   Vista inicial: Su historial de citas futuras, resultados de laboratorio listos para descargar y recetas activas.
    *   Opción prominente para "Agendar Nueva Cita" (Consume `GET /api/v1/appointments/slots` y `POST /api/v1/appointments`).
    *   **Restricciones de UI:** El Sidebar no tiene menús de IAM ni de edición de clínicas.

---

## 5. Próximos Pasos para el Desarrollo Frontend

1.  **Configurar Next.js + TailwindCSS + Zustand/Context.**
2.  **Crear el servicio API (`axios` o `fetch` config):** Configurar un interceptor que inyecte automáticamente el token en el Header `Authorization: Bearer <token>` para todas las llamadas al backend.
3.  **Implementar el Formulario de Login:** Integrarlo con `POST /auth/login` y guardar el token.
4.  **Implementar `middleware.ts`:** Validar las rutas `/admin/*`, `/doctor/*` y `/patient/*`.
5.  **Construir la pantalla IAM de Admin:** Es la más rápida de probar ya que el endpoint ya está 100% completado en el Backend.
