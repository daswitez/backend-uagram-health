# UAGRM Health - Backend

Backend del ecosistema clínico **UAGRM Health**, construido como **monolito modular** con Spring Boot 3.4 y Java 21.

## Nota importante sobre nombres

El nombre correcto del producto es **UAGRM Health**.

Sin embargo, el código fuente todavía conserva el typo histórico `ugram` en paquetes, clases, variables y nombres técnicos internos. Eso **no debe cambiarse por ahora** porque rompería la ejecución y las integraciones existentes.

En consecuencia:

- en **documentación funcional y de producto** se usará `UAGRM Health`
- en **referencias técnicas al código** se respetará el namespace real actual, por ejemplo `bo.edu.uagrm.ugram`

## Estado actual del backend

Estado verificado del proyecto:

- `identity` implementado y usable
- `common` implementado
- `storage` con servicio MinIO implementado
- `scheduling` con perfil y disponibilidad semanal del médico ya expuestos, pero sin slots/citas completas
- `emr` y `laboratory` con entidades/migraciones base, pero sin API completa
- frontend ya integrado con rutas, roles y vistas por perfil

La seguridad y el diseño funcional deben seguir dos principios obligatorios:

1. **Mínimo privilegio**
2. **Need to know**

## Requisitos

- JDK 21
- Docker y Docker Compose
- Opcional: Postman o Insomnia

No hace falta instalar Gradle; el proyecto incluye `./gradlew`.

## Cómo levantar el entorno

### 1. Variables de entorno

```bash
cp .env.example .env
```

### 2. Infraestructura

```bash
docker compose -f docker/docker-compose.yml up -d postgres minio minio-init
```

Servicios esperados:

- PostgreSQL en `localhost:5433`
- MinIO API en `localhost:9000`
- MinIO Console en `localhost:9001`

### 3. Backend

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

API local:

```text
http://localhost:8080/api/v1
```

## Usuarios seed

La migración `V5__seed_initial_data.sql` crea usuarios de prueba. La contraseña válida para todos es:

```text
Test_123!
```

| Rol | Identificador |
|---|---|
| `ADMIN` | `admin@uagrm.edu.bo` |
| `DOCTOR` | `dr.martinez@uagrm.edu.bo` |
| `STUDENT` | `12345678` |
| `LAB_TECH` | `lab.garcia@uagrm.edu.bo` |

Ejemplo de login:

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
  "identifier": "12345678",
  "password": "Test_123!"
}'
```

## Endpoints implementados hoy

- `POST /auth/register/patient`
- `POST /auth/login`
- `POST /auth/refresh`
- `GET /profile`
- `POST /admin/users/staff`
- `GET /doctors/me/profile`
- `PUT /doctors/me/profile`
- `GET /doctors/me/availability`
- `PUT /doctors/me/availability`

Los demás endpoints documentados deben tratarse como contrato objetivo hasta que exista controlador real.

## Orden recomendado de lectura

1. `AGENTS.md`
2. `docs/estado_actual/01_ESTADO_REAL.md`
3. `docs/arquitectura/02_IAM_ROLES_PERMISOS.md`
4. `docs/funcionalidad/BACKLOG_PRIORIZADO.md`
5. `docs/endpoints/01_API_REST_ACTUAL.md`

## Validación rápida

```bash
./gradlew test
```

Hoy ese comando pasa, pero todavía no hay suite real en `src/test`.
