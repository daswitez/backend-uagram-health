# UAGRM Health: Filosofía y Principios Fundamentales

## 1. Visión General

**UAGRM Health** busca modernizar la gestión de salud universitaria de la UAGRM y reducir procesos manuales, filas y fragmentación de información clínica.

El ecosistema se apoya en dos superficies principales:

1. aplicación móvil para estudiantes
2. portal web clínico/administrativo para staff

## 2. Principios de producto

### 2.1 Seguridad antes que conveniencia

El sistema maneja datos privilegiados. La experiencia debe ser usable, pero nunca a costa de romper:

- mínimo privilegio
- need to know

### 2.2 Valor operativo real

No se priorizan features aisladas, sino flujos completos que resuelvan problemas reales:

- disponibilidad médica confiable
- reserva sin conflictos
- atención clínica segura
- laboratorio con trazabilidad

### 2.3 Continuidad asistencial

El sistema debe permitir seguir el recorrido:

1. agenda
2. atención
3. receta u orden
4. resultado
5. consulta posterior

### 2.4 Desarrollo ágil por capacidades

El backlog describe capacidades de producto, no una lista rígida de tickets. Una historia puede representar una línea transversal completa y luego dividirse en tareas más pequeñas durante planificación o ejecución.

Ejemplos:

- agenda médica puede abrir tareas de perfil, disponibilidad, feriados, bloqueos, slots y citas
- EMR contextual puede abrir tareas de relación asistencial, DTOs mínimos, auditoría, drafts y recetas
- laboratorio puede abrir tareas de catálogo, orden, storage, aprobación y descarga

La regla práctica es mantener trazabilidad: cada tarea nueva debe poder explicarse como parte de una capacidad mayor y debe conservar mínimo privilegio y need to know.

## 3. Principios UX

- cero estados vacíos sin explicación
- autoguardado y recuperación donde haya riesgo de pérdida clínica
- velocidad y baja carga cognitiva para web clínica
- autoservicio claro y simple para estudiantes

## 4. Principios técnicos

- monolito modular
- contratos HTTP uniformes
- backend como fuente real de autorización
- trazabilidad e inmutabilidad progresiva en EMR

## 5. Criterio de priorización

Cuando haya dudas sobre qué construir primero, se prioriza:

1. lo que desbloquea flujo real de atención
2. lo que reduce riesgo clínico u operativo
3. lo que permite al frontend ya integrado trabajar con datos reales
4. lo que deja contratos backend claros para que Jira, documentación y código avancen alineados
