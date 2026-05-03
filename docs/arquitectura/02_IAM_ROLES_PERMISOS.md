# Identity and Access Management (IAM) - Ugram Health

Este documento define la estrategia de control de acceso para el ecosistema clínico Ugram Health, basada en los estándares de seguridad médica, implementando dos principios fundamentales de ciberseguridad:

1. **Principio del Mínimo Privilegio (PoLP):** Cada usuario tiene únicamente los permisos estrictamente necesarios para realizar su trabajo.
2. **Principio de "Need to Know" (Necesidad de Saber):** El acceso a la información clínica sensible (Fichas Médicas) está restringido criptográficamente y a nivel de aplicación; un médico solo puede ver los datos de los pacientes que está tratando activamente.

---

## 1. Definición de Roles (RBAC - Role Based Access Control)

El sistema soporta los siguientes roles, escalables a medida que la clínica crece:

### 🟢 Pacientes (Pacientes / Estudiantes)
- **Rol en BD:** `STUDENT` (o `PATIENT`)
- **Descripción:** Usuario final del sistema.
- **Permisos:** 
  - Solo pueden leer su **propio** historial clínico y resultados de laboratorio.
  - Pueden agendar citas para sí mismos.
- **Restricción:** No pueden ver información de absolutamente ningún otro usuario en el sistema.

### 🔵 Personal Médico (Doctores / Especialistas)
- **Rol en BD:** `DOCTOR`
- **Descripción:** Profesionales de la salud encargados de la atención clínica.
- **Permisos:**
  - Leer/Escribir en la Ficha Clínica Electrónica (EMR) de los pacientes que tienen cita con ellos.
  - Emitir recetas (Prescriptions) y órdenes de laboratorio.
- **Restricción (Need to Know):** No deberían poder explorar libremente las fichas de pacientes que no están bajo su cuidado. No pueden crear cuentas de otros médicos.

### 🟡 Personal de Laboratorio (Laboratoristas)
- **Rol en BD:** `LAB_TECH`
- **Descripción:** Especialistas clínicos técnicos.
- **Permisos:**
  - Ver órdenes de laboratorio pendientes.
  - Subir resultados (Archivos PDF a MinIO) y cerrar órdenes.
- **Restricción:** No pueden emitir recetas, no pueden modificar la ficha clínica principal, no ven diagnósticos confidenciales que no estén relacionados con la orden de laboratorio.

### 🔴 Administradores (IT / Recursos Humanos)
- **Rol en BD:** `ADMIN`
- **Descripción:** Gestores del sistema y del personal.
- **Permisos:**
  - Dar de alta o dar de baja al personal médico, secretarios y laboratoristas.
  - Gestionar el catálogo de servicios (ej. tipos de exámenes de laboratorio).
- **Restricción Crítica:** **Un administrador NO tiene acceso a leer las Fichas Clínicas de los pacientes**. El administrador gestiona el sistema, no hace medicina.

### ⚪ Futuros Roles Planificados
- **`SECRETARY` (Administrativos / Triage):** Podrán gestionar agendas, confirmar citas y facturar, pero tendrán la Ficha Clínica ofuscada (no pueden ver diagnósticos).
- **`AUDITOR` (Auditoría Médica / Legal):** Acceso de solo lectura a los registros inmutables (Blockchain) para revisar trazabilidad de cambios en caso de negligencia médica, sin poder modificar nada.

---

## 2. Flujos de Creación de Usuarios (Provisioning)

Para respetar el "Principio del Mínimo Privilegio", la creación de cuentas está estrictamente separada en dos flujos:

### Flujo A: Auto-Registro Público (Solo Pacientes)
- **Endpoint:** `POST /api/v1/auth/register/patient`
- **Autorización:** Público (Sin token).
- **Proceso:**
  1. El usuario descarga la App Móvil.
  2. Ingresa su C.I., Email, Nombre, Contraseña y datos básicos (Tipo de Sangre, etc).
  3. El sistema valida que el C.I. y Email no existan.
  4. Crea un registro en la tabla `users` forzando el rol `STUDENT`.
  5. Crea el perfil médico vacío en la tabla `patients`.
- **Seguridad:** Es imposible que alguien se auto-registre pasando un parámetro oculto `"rol": "ADMIN"` o `"DOCTOR"`. El endpoint tiene el rol *hardcodeado*.

### Flujo B: Aprovisionamiento Backoffice (Solo Staff)
- **Endpoint:** `POST /api/v1/admin/users/staff`
- **Autorización:** Protegido (Requiere token JWT con rol `ADMIN`).
- **Proceso:**
  1. El Administrador verifica las credenciales físicas del nuevo empleado (C.I., Título, Matrícula Profesional).
  2. El Administrador ingresa al portal web (dashboard).
  3. Rellena un formulario enviando los datos, el rol a asignar (`DOCTOR` o `LAB_TECH`) y la información específica (ej. Especialidad y Matrícula para el médico).
  4. El sistema crea el `User` y su respectiva extensión (`doctors` o `lab_techs`).
  5. El sistema envía un correo al empleado con una contraseña temporal para su primer inicio de sesión.
- **Seguridad:** Previene la suplantación de identidad médica. Centraliza el control de altas y bajas laborales.
