# AGENTS.md

Guía rápida para futuros chats sobre este repositorio.

## 1. Regla de nombres

- nombre correcto del producto: **UAGRM Health**
- nombre técnico actual en código: `ugram`

Esto significa:

- en documentación hablar de `UAGRM Health`
- en referencias al código respetar `bo.edu.uagrm.ugram`, `UgramHealthApplication`, `ugram_health`, etc.

No cambiar identificadores técnicos salvo que el usuario pida explícitamente una migración controlada del namespace.

## 2. Estado real

Implementado:

- `common`
- `identity`
- seguridad JWT
- `ObjectStorageService`
- migraciones Flyway

Parcial avanzado:

- `scheduling`

En `scheduling` ya existen endpoints para perfil médico, disponibilidad semanal, parámetros de agenda, readiness, calendario institucional, bloqueos y consulta de slots. Todavía falta la operación completa de citas: reserva, cancelación, agenda semanal, cambio de estado y reprogramación.

Scaffold o modelo base:

- `emr`
- `laboratory`

Planeado:

- `notification`
- blockchain real

## 3. Restricciones funcionales clave

El sistema maneja información privilegiada. Toda propuesta debe respetar:

1. **Mínimo privilegio**
2. **Need to know**

Consecuencias:

- `ADMIN` no accede a EMR
- `DOCTOR` solo ve pacientes bajo contexto asistencial válido
- `LAB_TECH` solo ve lo necesario para procesar órdenes
- `STUDENT` solo opera sobre su propia información

## 4. Estado del frontend

El frontend ya está integrado con:

- rutas por rol
- layouts por perfil
- flujo de login conectado

Eso no reemplaza la autorización backend.

## 5. Fuente de verdad

Orden recomendado:

1. `AGENTS.md`
2. `docs/estado_actual/01_ESTADO_REAL.md`
3. `docs/arquitectura/02_IAM_ROLES_PERMISOS.md`
4. `docs/funcionalidad/BACKLOG_PRIORIZADO.md`
5. `src/main/resources/db/migration/`
6. controladores/servicios reales en `src/main/java`

## 6. Cómo leer el backlog

El desarrollo se lleva de forma ágil. Las historias en `docs/funcionalidad/BACKLOG_PRIORIZADO.md` representan capacidades funcionales o transversales, no necesariamente tickets atómicos.

Consecuencias:

- una historia puede descomponerse en varias tareas técnicas, bugs, subtareas o historias Jira
- una capacidad transversal como IAM, agenda, EMR contextual, storage o laboratorio puede tocar varios módulos
- antes de implementar, verificar siempre si la historia ya está parcial o totalmente cubierta por código real
- si se crea una tarea nueva, debe conservar trazabilidad hacia la capacidad o historia madre
- actualizar documentación cuando cambie el estado real, no solo cuando cambie Jira

## 7. Prioridad técnica sugerida

1. reserva, cancelación y operación básica de citas
2. agenda semanal y reprogramación médica
3. EMR con control contextual
4. laboratorio y storage operativo
5. notificaciones
6. blockchain real
