# ADR: Monolito Modular vs Microservicios

**Fecha:** Abril 2026
**Estado:** Aceptado

## Contexto

El sistema UAGRM Health requiere gestionar múltiples dominios complejos: Identidad de estudiantes, Agendamiento de citas, Historia Clínica (EMR), y Laboratorio. La documentación original sugería evaluar Microservicios o un Monolito Modular.

## Decisión

Se ha decidido implementar un **Monolito Modular** utilizando Spring Boot.

### Razones:
1. **Tamaño del Equipo:** La gestión operativa de una arquitectura de microservicios pura (Service Mesh, Tracing distribuido, API Gateways, múltiples bases de datos) requiere un equipo DevOps dedicado. Un monolito modular puede ser mantenido por 1-3 desarrolladores.
2. **Consistencia Transaccional:** Al estar en una sola base de datos (PostgreSQL), podemos usar transacciones locales (`@Transactional`) sin recurrir a patrones complejos como *Sagas* o consistencia eventual.
3. **Latencia Cero:** Las llamadas entre módulos (ej. Scheduling llamando a Identity) se resuelven en memoria (In-Process) en lugar de saltos de red.

## Reglas de Implementación (Package-by-Feature)

Para garantizar que el Monolito Modular no se convierta en un "Monolito Espagueti", se imponen estas reglas de arquitectura:

1. **Agrupación por Dominio:** El código no se organiza por capas globales (`controllers/`, `services/`), sino por dominios funcionales (`bo.edu.uagrm.ugram.identity`, `bo.edu.uagrm.ugram.emr`).
2. **Encapsulamiento de Datos:** Un módulo **NO** puede acceder al repositorio JPA de otro módulo. Si el módulo `emr` necesita datos del usuario, debe solicitarlos llamando a los métodos públicos del `AuthService` o `UserService` del módulo `identity`.
3. **Mapeo Claro (Foreign Keys):** Las entidades cruzan fronteras a través de identificadores (UUIDs) o mapeos `@ManyToOne` justificados. En el futuro, si un módulo se extrae a microservicio, esos `@ManyToOne` se refactorizarán para guardar solo el `UUID` y usar clientes HTTP/gRPC.
