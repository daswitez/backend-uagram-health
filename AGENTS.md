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

Parcial o scaffold:

- `scheduling`
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

## 6. Prioridad técnica sugerida

1. perfil médico y disponibilidad
2. feriados y bloqueos
3. slots y citas
4. EMR con control contextual
5. laboratorio y storage
6. notificaciones
7. blockchain real
