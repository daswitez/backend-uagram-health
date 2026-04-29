# Ugram Health - Backend

Este es el backend del ecosistema médico **Ugram Health**, construido sobre una arquitectura de **Monolito Modular** utilizando Spring Boot 3.4 y Java 21.

Toda la documentación arquitectónica profunda, decisiones de diseño, esquema de base de datos y endpoints se encuentran en la carpeta `/docs`.

---

## 🚀 Requisitos Previos

Para correr este proyecto necesitas tener instalado en tu máquina local:

1. **Java Development Kit (JDK) 21**
2. **Docker y Docker Compose** (para levantar la base de datos PostgreSQL y MinIO).
3. *(Opcional)* Postman para probar los endpoints.

*Nota: No necesitas instalar Gradle, el proyecto incluye un Wrapper (`gradlew`) que lo descargará automáticamente.*

---

## 🛠️ Cómo Correr el Proyecto (Paso a Paso)

### 1. Configurar Variables de Entorno

El proyecto incluye una plantilla de variables de entorno. Copia el archivo de ejemplo para crear el tuyo propio:

```bash
# En la raíz del proyecto
cp .env.example .env
```
*(Puedes dejar los valores por defecto que trae para desarrollo local).*

### 2. Levantar la Infraestructura (Base de Datos y Almacenamiento)

Utiliza Docker Compose para iniciar PostgreSQL y MinIO en segundo plano. Esto también ejecutará los scripts de inicialización necesarios:

```bash
# Si usas Docker Compose V2 (más reciente):
docker compose -f docker/docker-compose.yml up -d postgres minio minio-init

# Si usas Docker Compose V1 (antiguo):
docker-compose -f docker/docker-compose.yml up -d postgres minio minio-init
```

Para verificar que los contenedores están corriendo correctamente:
```bash
docker ps
```
Deberías ver a `ugram-postgres` corriendo en el puerto **5433** (lo cambiamos para no chocar con tu Postgres local) y a `ugram-minio` en los puertos 9000/9001.

### 3. Detener la Infraestructura (Opcional)
Cuando termines de trabajar y quieras apagar la base de datos sin perder los datos:
```bash
docker-compose -f docker/docker-compose.yml down
```

### 4. Ejecutar la Aplicación Spring Boot

Una vez que la base de datos está activa, puedes iniciar el servidor Backend. Flyway se encargará de crear todas las tablas e insertar los datos de prueba automáticamente al arrancar.

```bash
# En Linux / macOS
./gradlew bootRun --args='--spring.profiles.active=dev'

# En Windows (CMD o PowerShell)
gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

Si todo sale bien, verás en la consola que el servidor inicia en el puerto **8080**.

---

## 🧪 Usuarios de Prueba (Seed Data)

Para que puedas empezar a probar los endpoints (ej. hacer login) inmediatamente, la base de datos se inicializa con los siguientes usuarios de prueba. **La contraseña para todos los usuarios listados a continuación es la que le corresponde en la tabla** (por defecto configuradas en la migración `V5`).

| Rol | Identificador (Email o R.U.) | Contraseña |
|---|---|---|
| **Administrador** | `admin@uagrm.edu.bo` | `admin123` |
| **Médico** | `dr.martinez@uagrm.edu.bo` | `doctor123` |
| **Estudiante** | `220012345` | `student123` |
| **Laboratorista** | `lab.garcia@uagrm.edu.bo` | `lab123` |

### Haciendo tu primera petición

Para probar que la app funciona, abre otra terminal y ejecuta este cURL para iniciar sesión como Estudiante:

```bash
curl --location 'http://localhost:8080/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "identifier": "220012345",
    "password": "student123"
}'
```
El sistema te devolverá tu `accessToken` y `refreshToken`.

---

## 📁 ¿Dónde encuentro más información?

Dirígete a la carpeta `/docs` para entender a profundidad el proyecto:

- `/docs/arquitectura/`: Entiende el stack, el Docker Compose y por qué se estructuran los módulos así.
- `/docs/endpoints/`: Aquí está la lista completa de rutas de la API y comandos `curl` listos para usar en Postman.
- `/docs/base_de_datos/`: Diagramas Entidad-Relación y explicación de la inmutabilidad de la Ficha Clínica.
- `/docs/funcionalidad/`: El backlog priorizado de lo que hay que construir (Roadmap).
