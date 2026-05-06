# IAM, Roles y Permisos

## 1. Objetivo

**UAGRM Health** maneja información clínica sensible. El modelo IAM no se limita a autenticación; define también **qué puede hacer cada rol, qué puede ver y bajo qué contexto**.

El sistema debe sostener dos principios no negociables:

1. **Mínimo privilegio**
2. **Need to know**

## 2. Principios rectores

### 2.1 Mínimo privilegio

Cada usuario recibe solo los permisos estrictamente necesarios para operar.

Consecuencias prácticas:

- un `ADMIN` no lee fichas clínicas
- un `LAB_TECH` no prescribe ni edita EMR
- un `STUDENT` no accede a datos ajenos

### 2.2 Need to know

No basta con tener un rol válido. También debe existir una **relación asistencial u operativa legítima**.

Ejemplos:

- un médico no debe explorar historiales fuera de su contexto asistencial
- un laboratorista no debe ver diagnóstico completo si solo necesita procesar una orden
- un estudiante no debe inferir agendas o datos de terceros

## 3. Capas de enforcement

### 3.1 Backend

- validación de JWT
- controles por rol
- filtros por propietario o contexto clínico
- DTOs limitados por caso de uso

### 3.2 Frontend

El frontend ya cuenta con rutas y vistas separadas por rol, lo cual mejora UX y reduce exposición accidental.

Pero:

- el frontend **no reemplaza** la autorización backend
- ocultar botones no es una medida de seguridad suficiente

## 4. Roles actuales

| Rol | Alcance principal | Restricción crítica |
|---|---|---|
| `ADMIN` | aprovisionamiento de personal, catálogos, calendario institucional | no accede a EMR |
| `DOCTOR` | agenda, EMR, recetas, órdenes | solo pacientes bajo contexto asistencial válido |
| `LAB_TECH` | worklist de laboratorio, carga y aprobación de resultados | acceso limitado al contexto de la orden |
| `STUDENT` | autoservicio sobre datos propios | no accede a datos de terceros |
| `RECEPTIONIST` | planificado | agenda y operación sin acceso diagnóstico |

## 5. Matriz resumida de permisos

| Capacidad | ADMIN | DOCTOR | LAB_TECH | STUDENT |
|---|---|---|---|---|
| Login y refresh | Sí | Sí | Sí | Sí |
| Ver propio perfil | Sí | Sí | Sí | Sí |
| Alta de staff | Sí | No | No | No |
| Configurar agenda médica | No | Sí | No | No |
| Reservar cita propia | No | No | No | Sí |
| Ver agenda médica propia | No | Sí | No | No |
| Crear ficha clínica | No | Sí | No | No |
| Ver EMR completo | No | Sí, con contexto | No | No |
| Crear orden de laboratorio | No | Sí | No | No |
| Procesar resultados | No | No | Sí | No |
| Descargar resultado propio | No | No | No | Sí |

## 6. Reglas por dominio

### 6.1 Identity

- el auto-registro público solo crea `STUDENT`
- el alta de staff requiere `ADMIN`
- no permitir elevación de privilegios desde payloads públicos

### 6.2 Scheduling

- el médico administra su disponibilidad
- el estudiante solo reserva dentro de slots válidos
- un horario bloqueado, feriado o excepción no debe exponerse como disponible

### 6.3 EMR

- el médico solo accede al historial de pacientes con vínculo asistencial válido
- las correcciones son append-only
- el administrador queda excluido del contenido clínico

### 6.4 Laboratory

- el laboratorista ve órdenes e información mínima para procesarlas
- el acceso a archivos y resultados debe estar ligado a la orden y al rol
- el estudiante solo descarga sus propios resultados publicados

## 7. Aprovisionamiento

### 7.1 Auto-registro público

- endpoint: `POST /api/v1/auth/register/patient`
- resultado: crea `User` + `Patient`
- rol forzado: `STUDENT`

### 7.2 Alta de personal por administración

- endpoint: `POST /api/v1/admin/users/staff`
- autorización: `ADMIN`
- crea usuario de staff y perfil complementario cuando aplica

## 8. Regla para historias futuras

Toda historia nueva debe responder explícitamente:

1. qué rol la ejecuta
2. qué datos necesita realmente ver
3. qué datos debe quedar imposibilitado de ver
4. qué validación de contexto aplica

Si una historia no resuelve esas preguntas, no está lista para implementarse.
