# Ugram Health: Funcionalidades Core e Historias de Usuario (Backlog Completo)

Este documento detalla las funcionalidades principales del ecosistema y sus respectivas Historias de Usuario (User Stories), separadas por el entorno de acceso (Móvil vs Web).

## 1. Aplicación Móvil (Estudiantes)

El objetivo principal es la autogestión y reducir el flujo físico hacia la clínica FUSUM.

### Funcionalidad 1: Autenticación y Perfil Digital
*   **Epic:** Como estudiante de la UAGRM, necesito acceder al sistema de forma segura para gestionar mis servicios de salud y tener una credencial digital válida.
*   **Historias de Usuario:**
    *   `US-M01`: Como estudiante, quiero iniciar sesión utilizando mi Registro Universitario (R.U.) y contraseña institucional, para no tener que crear una cuenta nueva.
    *   `US-M02`: Como estudiante, quiero recuperar mi contraseña mediante OTP a mi celular registrado, para no tener que ir presencialmente a la universidad.
    *   `US-M03`: Como estudiante, quiero visualizar una credencial digital (Carnet FUSUM) con un código QR dinámico en mi perfil, para validar mi identidad en la recepción de la clínica si asisto presencialmente.
    *   `US-M04`: Como estudiante, quiero ver mis datos personales básicos y grupo sanguíneo en mi perfil, para confirmar que mi información médica principal está correcta.

### Funcionalidad 2: Gestión de Citas (Agendamiento)
*   **Epic:** Como estudiante, necesito poder programar, ver y cancelar citas médicas desde mi teléfono para evitar largas filas matutinas.
*   **Historias de Usuario:**
    *   `US-M05`: Como estudiante, quiero ver un calendario con los horarios disponibles de diferentes especialidades (Medicina General, Odontología), para seleccionar el que se ajuste a mis clases.
    *   `US-M06`: Como estudiante, quiero reservar un turno específico (ej. Martes a las 10:00 AM) recibiendo confirmación inmediata en la app.
    *   `US-M07`: Como estudiante, quiero poder cancelar una cita agendada con al menos 2 horas de anticipación, para liberar el espacio para otro compañero.
    *   `US-M08`: Como sistema, quiero enviar notificaciones push al estudiante 1 hora antes de su cita, para reducir el índice de ausentismo.

### Funcionalidad 3: Visualización de Resultados e Historial
*   **Epic:** Como estudiante, necesito acceder a los resultados de mis exámenes y a mis recetas de forma digital y permanente.
*   **Historias de Usuario:**
    *   `US-M09`: Como estudiante, quiero recibir una notificación push cuando mis resultados de laboratorio estén listos.
    *   `US-M10`: Como estudiante, quiero visualizar una lista de mis exámenes recientes y poder descargar el resultado detallado en formato PDF.
    *   `US-M11`: Como estudiante, quiero ver las recetas médicas activas que me han emitido, incluyendo la dosis y duración, para no depender del papel.
    *   `US-M12`: Como estudiante, quiero ver un historial básico cronológico de las veces que he sido atendido en el semestre.

---

## 2. Portal Web Administrativo/Clínico (Desktop)

El objetivo es maximizar la eficiencia del personal de salud, manejar grandes volúmenes de pacientes y mantener el registro médico seguro.

### Funcionalidad 4: Login Institucional y Roles (Local)
*   **Epic:** Como personal de FUSUM, necesito acceder a las herramientas correspondientes a mi cargo de manera rápida y segura.
*   **Historias de Usuario:**
    *   `US-W01`: Como médico o administrativo, quiero ingresar usando mi correo institucional (`@uagrm.edu.bo`) y contraseña a través del sistema de autenticación local (JWT), para no depender de servicios externos en la red de la clínica.
    *   `US-W02`: Como sistema, debo asegurar que un usuario con rol 'Recepcionista' no pueda acceder a las rutas de 'Historia Clínica', protegiendo la confidencialidad diagnóstica mediante validación de roles en el token JWT.

### Funcionalidad 5: The "Kanban-Calendar" (Agenda Maestra)
*   **Epic:** Como médico, necesito visualizar de forma masiva y gestionar fácilmente mi carga de pacientes diarios y semanales.
*   **Historias de Usuario:**
    *   `US-W03`: Como médico, quiero ver una vista de calendario semanal en formato grilla, donde cada cita es una tarjeta con el nombre del paciente y estado (Pendiente, En Espera, Atendido).
    *   `US-W04`: Como médico, quiero poder arrastrar (Drag & Drop) una tarjeta de cita de un horario a otro para reprogramarla, confirmando el cambio mediante un modal.
    *   `US-W05`: Como médico, quiero usar filtros superiores rápidos para ver solo "Citas Urgentes" o "Pacientes en Sala de Espera".

### Funcionalidad 6: Electronic Medical Record (EMR) - Split View
*   **Epic:** Como médico, necesito registrar el diagnóstico y tratamiento de forma ágil, mientras reviso el historial del paciente en la misma pantalla.
*   **Historias de Usuario:**
    *   `US-W06`: Como médico, quiero abrir la Ficha Médica y ver la pantalla dividida: a la izquierda el historial completo cronológico y alergias, y a la derecha el editor de la consulta actual.
    *   `US-W07`: Como médico, quiero usar un editor Rich Text para redactar la anamnesis y examen físico sin restricciones de formato.
    *   `US-W08`: Como médico, quiero utilizar macros/snippets de texto (ej. escribiendo `/gripe`) para autocompletar planes de tratamiento estándar aprobados por FUSUM, ahorrando tiempo de tipeo.
    *   `US-W09`: Como médico, quiero botones rápidos para emitir órdenes de laboratorio y recetas de medicamentos directamente desde el editor de la ficha médica actual.
    *   `US-W10`: Como médico, requiero que lo que estoy tipeando se autoguarde temporalmente, para no perder información si la pestaña del navegador se cierra por accidente.

### Funcionalidad 7: Módulo de Laboratorio Central
*   **Epic:** Como personal de laboratorio, necesito gestionar eficientemente las órdenes solicitadas y procesar los resultados masivamente.
*   **Historias de Usuario:**
    *   `US-W11`: Como laboratorista, quiero ver una tabla principal (DataGrid) con todas las órdenes pendientes, priorizando las marcadas como "Urgentes" en color rojo.
    *   `US-W12`: Como laboratorista, quiero poder seleccionar múltiples resultados de pruebas de rutina (ej. 20 hemogramas normales) y usar un botón "Aprobar Grupo" (Batch Approval) para enviarlos todos simultáneamente a los estudiantes.
    *   `US-W13`: Como laboratorista, quiero tener una zona de "Arrastrar y Soltar" (Dropzone) para subir archivos PDF o DICOM (rayos x) y adjuntarlos directamente al historial del paciente.
    *   `US-W14`: Como médico solicitante, quiero poder descargar un PDF estético, validado y completo de los exámenes (ej. Hemograma) emitidos por el laboratorio.

### Funcionalidad 8: Blockchain e Inmutabilidad (Backend Core)
*   **Epic:** Como director médico o auditor, necesito garantizar la validez legal de los registros médicos.
*   **Historias de Usuario:**
    *   `US-B01`: Como sistema, debo generar un hash criptográfico al guardar una Ficha Médica y enviarlo a la red Blockchain para garantizar su inmutabilidad.
    *   `US-B02`: Como sistema, no debo permitir que un registro médico en la base de datos sea modificado (Update). Si hay un error, el médico debe crear una "Nota de Corrección" anexa al registro original.
