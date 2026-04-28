# Ugram Health: Filosofía y Principios Fundamentales

Este documento describe la base filosófica, los principios de diseño y los objetivos principales del ecosistema Ugram Health.

## 1. Visión General

Ugram Health es un ecosistema digital integral diseñado para modernizar y optimizar la gestión de la salud en la Universidad Autónoma Gabriel René Moreno (UAGRM), específicamente a través de su seguro médico universitario (FUSUM).

El sistema busca reemplazar procesos obsoletos, basados en papel y dependientes de interacciones físicas ineficientes, con una plataforma digital ágil, segura y centrada en el usuario, compuesta por dos pilares principales:

1.  **Aplicación Móvil (Estudiantes):** Orientada a la autogestión, prevención y acceso rápido a servicios.
2.  **Portal Web Administrativo/Clínico (Desktop):** Orientado a médicos, especialistas y personal administrativo, diseñado para alta productividad y gestión masiva.

## 2. Filosofía de Diseño: "Swiss Modernism 2.0 Adaptativo"

La estética y la experiencia de usuario de Ugram Health no son meramente decorativas; son herramientas funcionales diseñadas para reducir la carga cognitiva y transmitir autoridad. Se rigen por los principios del **Swiss Modernism 2.0**:

*   **Autoridad y Confianza:** El sistema debe sentirse como un entorno clínico seguro, preciso e institucional. Se abandona la estética "divertida" o genérica de las aplicaciones de consumo en favor de un diseño sobrio y profesional.
*   **Minimalismo Funcional:** Eliminación de ruido visual ("AI slop", sombras excesivas, animaciones exageradas). El espacio negativo (blanco puro) se utiliza para estructurar la información y reducir la fatiga visual de los médicos que usan el sistema durante horas.
*   **Jerarquía Tipográfica Clara:** Uso estricto de la tipografía "Inter" para garantizar la máxima legibilidad de historiales médicos densos, especialmente en pantallas pequeñas.
*   **Cromatismo Semántico:** Los colores no decoran, informan.
    *   **Azul Royal (`#2563EB`):** Acción primaria, identidad corporativa (FUSUM/UAGRM).
    *   **Blanco Puro (`#FFFFFF`):** Entorno clínico, limpieza, espacio.
    *   **Verde Esmeralda (`#10B981`):** Éxito, confirmación, valores normales.
    *   **Rojo (`#DC2626`):** Alertas críticas, peligro, alergias.
    *   **Ámbar (`#F59E0B`):** Precaución, espera.
*   **Ausencia de Bordes Cortantes:** El sistema evita las esquinas agudas (`border-radius` amplio en tarjetas, botones y modales) para transmitir una sensación más humana, orgánica y menos amenazante, contrastando con la rigidez de los datos médicos.

## 3. Principios de Experiencia de Usuario (UX)

1.  **Diseño Anti-Amputación (Auto-Guards):** En el portal médico, la información crítica (como notas de anamnesis en curso) debe ser protegida contra cierres accidentales o fallos de red. El sistema debe priorizar el guardado local temporal o la recuperación de estado.
2.  **Cero Vacío (Empty States Significativos):** Ninguna pantalla debe mostrar un vacío incomprensible. La ausencia de datos (ej. "No hay citas hoy") debe ir acompañada de un mensaje claro, una ilustración acorde al diseño y una llamada a la acción (ej. "Programar nueva cita").
3.  **Transiciones No Destructivas:** Las acciones importantes (confirmar ficha, emitir receta) no deben realizar cortes abruptos. Deben utilizar retroalimentación visual clara (spinners, checks de confirmación) que informen al usuario del éxito de la operación antes de cambiar el contexto.
4.  **Eficiencia por encima de todo (Web):** El portal web (Desktop) está diseñado para la velocidad. Soporta atajos de teclado (snippets con `/`), interfaces de doble panel (Split View) para lectura y escritura simultánea, y acciones por lotes (Batch Approval) para evitar tareas repetitivas.
5.  **Autogestión (Móvil):** La app móvil empodera al estudiante, reduciendo las colas en FUSUM al permitir agendar citas, consultar resultados y gestionar su credencial digital de forma asíncrona.

## 4. Arquitectura de Alto Nivel y Seguridad

La arquitectura de Ugram Health se fundamenta en tres pilares de ingeniería:

1.  **Robustez y Escalabilidad:** El backend debe estar preparado para manejar el volumen de miles de estudiantes de la UAGRM y la concurrencia de consultas en horarios pico.
2.  **Seguridad Inquebrantable e Inmutabilidad (Blockchain):** Dado que se maneja información biológica y diagnóstica altamente sensible, el historial médico (EMR) será almacenado utilizando tecnología **Blockchain**.
    *   **Por qué Blockchain:** Garantiza que una vez que un diagnóstico o receta es emitido y firmado por un médico autorizado, no pueda ser alterado retroactivamente sin dejar una huella auditable criptográficamente segura. Esto protege legalmente al médico y garantiza la integridad de los datos del paciente.
3.  **Control de Acceso Basado en Roles (RBAC) Estricto:** La plataforma diferencia claramente entre el acceso de un estudiante (móvil, autenticación rápida/OTP) y el acceso del personal de FUSUM (web, autenticación segura local mediante JWT e intranet), con permisos granulares (Médico General, Especialista, Recepcionista, Laboratorista).

---
*Fin del Documento de Filosofía.*
