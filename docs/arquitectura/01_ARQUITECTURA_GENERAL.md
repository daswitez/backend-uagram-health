# Arquitectura General

## 1. Resumen

**UAGRM Health** está planteado como un **monolito modular** backend-first, con separación por dominio y contratos HTTP uniformes para el frontend.

## 2. Regla de nombres

El producto debe documentarse como **UAGRM Health**.

El código fuente, sin embargo, mantiene el typo histórico `ugram` en nombres técnicos internos. La documentación debe reflejar ambos niveles:

- nombre de producto: `UAGRM Health`
- namespace técnico actual: `bo.edu.uagrm.ugram`

## 3. Stack real

- Java 21
- Spring Boot 3.4.4
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- MinIO
- Gradle Wrapper 8.11.1
- MapStruct
- Lombok
- OpenPDF

## 4. Paquete raíz actual

```text
src/main/java/bo/edu/uagrm/ugram/
```

Clase principal:

```text
bo.edu.uagrm.ugram.UgramHealthApplication
```

## 5. Módulos del backend

| Módulo | Responsabilidad | Estado actual |
|---|---|---|
| `common` | configuración, seguridad, excepciones, DTOs comunes | implementado |
| `identity` | autenticación, registro, perfiles base, staff admin | implementado |
| `scheduling` | disponibilidad, restricciones, slots y citas | parcial avanzado |
| `emr` | historia clínica, snippets, recetas, inmutabilidad | scaffold |
| `laboratory` | órdenes, resultados, lotes | scaffold |
| `storage` | integración con MinIO | servicio base implementado |
| `notification` | recordatorios y eventos push | planificado |

## 6. Reglas arquitectónicas

### 6.1 Separación por dominio

Se trabaja con `package-by-feature`, no con capas globales planas.

### 6.2 Integración entre módulos

Regla a mantener:

- un módulo no debe consumir directamente repositorios de otro módulo
- la integración entre dominios debe pasar por servicios

### 6.3 Seguridad transversal

IAM condiciona toda la arquitectura.

Principios obligatorios:

1. **Mínimo privilegio**
2. **Need to know**

### 6.4 Contrato uniforme

Todos los endpoints deben responder con `ApiResponse<T>`.

### 6.5 Backlog ágil

Las historias funcionales documentadas son capacidades del producto. No deben leerse como una descomposición técnica completa.

Reglas prácticas:

- una capacidad transversal puede generar varias tareas Jira
- una tarea puede tocar varios módulos si respeta las fronteras de dominio
- el estado de una capacidad se verifica contra código y migraciones, no solo contra el título de la historia
- cuando una tarea cambia el contrato HTTP, debe actualizar `docs/endpoints`

## 7. Relación con el frontend

El frontend ya está segmentado por rol y rutas.

Perfiles principales:

- `ADMIN`
- `DOCTOR`
- `LAB_TECH`
- `STUDENT`

La navegación del frontend no reemplaza autorización backend.

## 8. Infraestructura local

`docker/docker-compose.yml` levanta:

| Servicio | Uso | Puerto host |
|---|---|---|
| `postgres` | base de datos | `5433` |
| `minio` | object storage | `9000` |
| `minio-console` | consola MinIO | `9001` |
| `backend` | API Spring Boot | `8080` |

## 9. Orden técnico de construcción

1. reserva, cancelación y operación básica de citas
2. agenda semanal y reprogramación médica
3. EMR con control contextual
4. laboratorio y archivos
5. notificaciones
6. blockchain real
