# Backlog Priorizado de Desarrollo

## 1. Objetivo

Este backlog está ordenado por:

- valor funcional
- dependencias reales
- riesgo de seguridad

Reglas de priorización:

1. IAM, mínimo privilegio y need to know no son opcionales
2. no se habilita agenda del paciente sin disponibilidad médica confiable
3. no se habilita EMR sin control contextual
4. no se publica laboratorio sin pipeline completo de orden, archivo y aprobación

## 2. Estado base ya disponible

- auth JWT
- refresh token
- registro de estudiantes
- alta de staff por admin
- entidades y migraciones de scheduling, EMR y laboratory
- servicio base de MinIO

## 3. Backlog priorizado

### P1. US-I01 - Perfil profesional editable del médico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero editar mi perfil profesional y parámetros básicos de atención para que el sistema pueda usar datos reales en agenda y consulta.  
**Dependencias:** `identity` actual.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando consulta su perfil profesional, entonces ve especialidad, matrícula y datos públicos de atención.
2. Dado un médico autenticado, cuando actualiza su perfil, entonces el sistema persiste solo campos permitidos para su rol.
3. Dado un usuario que no es médico, cuando intenta usar estos endpoints, entonces el sistema responde `403`.

### P2. US-S01 - Configuración semanal de disponibilidad médica

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero definir mis horarios base por día de la semana para que el sistema pueda generar disponibilidad real.  
**Dependencias:** `US-I01`.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando registra franjas por día, entonces el sistema guarda la configuración semanal sin solapamientos.
2. Dado una franja inválida, cuando se intenta guardar, entonces el sistema rechaza la operación con error de validación.
3. Dado un médico con configuración guardada, cuando consulta su disponibilidad, entonces recupera exactamente las franjas vigentes.

### P3. US-S02 - Parámetros de agenda y duración de consulta

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero definir duración de consulta y reglas operativas para que los slots se generen con mi ritmo real de atención.  
**Dependencias:** `US-S01`.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando configura duración de consulta, entonces el motor de agenda usa ese valor al generar slots.
2. Dado un valor fuera de rango permitido, cuando se intenta guardar, entonces el sistema rechaza el cambio.
3. Dado una agenda ya configurada, cuando el médico actualiza la duración, entonces los slots futuros usan la nueva regla.

### P3.1. US-S10 - Estado de preparación de agenda médica

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero saber si mi agenda está realmente lista para operar para no habilitar disponibilidad al paciente con configuración incompleta o ambigua.  
**Dependencias:** `US-I01`, `US-S01`, `US-S02`.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando consulta el estado de su agenda, entonces el sistema indica si perfil profesional, disponibilidad semanal y duración de consulta están completos.
2. Dado una configuración incompleta o aún no confirmada explícitamente, cuando se evalúa la agenda, entonces el sistema la marca como no lista para publicar slots.
3. Dado una agenda marcada como no lista, cuando el estudiante intenta consultar disponibilidad de ese médico, entonces el sistema no expone slots para ese contexto.

### P4. US-A01 - Calendario institucional de feriados y jornadas parciales

**Actor:** `ADMIN`  
**Descripción:** Como administrador, quiero registrar feriados o jornadas parciales para que el sistema no ofrezca atención donde institucionalmente no corresponde.  
**Dependencias:** IAM actual.

**Criterios de aceptación**

1. Dado un administrador autenticado, cuando crea un feriado total, entonces ningún slot de ese día queda disponible.
2. Dado un administrador autenticado, cuando crea una jornada parcial, entonces solo quedan disponibles las horas no bloqueadas.
3. Dado un usuario sin rol `ADMIN`, cuando intenta modificar feriados, entonces el sistema responde `403`.

### P4.1. US-A02 - Consulta operativa del calendario institucional

**Actor:** `ADMIN`, `DOCTOR`  
**Descripción:** Como administrador o médico, quiero visualizar las restricciones institucionales vigentes para entender qué días y franjas ya están afectadas antes de operar sobre agenda y disponibilidad.  
**Dependencias:** `US-A01`.

**Criterios de aceptación**

1. Dado un usuario autorizado, cuando consulta el calendario institucional, entonces recibe feriados totales y jornadas parciales con fecha, rango y motivo.
2. Dado un rango de fechas, cuando se consulta el calendario, entonces el sistema devuelve solo las restricciones comprendidas en ese período.
3. Dado un usuario sin rol `ADMIN` ni `DOCTOR`, cuando intenta consultar el calendario institucional, entonces el sistema responde `403`.

### P4.2. US-A03 - Edición y eliminación de restricciones institucionales

**Actor:** `ADMIN`  
**Descripción:** Como administrador, quiero editar o eliminar feriados y jornadas parciales para corregir cambios operativos sin dejar restricciones institucionales obsoletas.  
**Dependencias:** `US-A01`, `US-A02`.

**Criterios de aceptación**

1. Dado un administrador autenticado, cuando actualiza una restricción institucional existente, entonces el sistema valida solapamientos y persiste la nueva configuración.
2. Dado una restricción institucional existente, cuando el administrador la elimina, entonces deja de afectar la disponibilidad futura.
3. Dado un usuario sin rol `ADMIN`, cuando intenta editar o eliminar restricciones institucionales, entonces el sistema responde `403`.

### P5. US-S03 - Bloqueos y excepciones puntuales del médico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero bloquear un día o un rango horario específico para reflejar ausencias, licencias o contingencias.  
**Dependencias:** `US-S01`, `US-S02`.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando registra un bloqueo puntual, entonces el rango afectado deja de ofrecerse como disponible.
2. Dado un bloqueo que coincide con citas futuras, cuando se intenta guardar, entonces el sistema reporta el conflicto.
3. Dado un bloqueo existente, cuando el médico lo elimina, entonces el rango vuelve a disponibilidad solo si no hay otra restricción activa.

### P5.2. US-S13 - Consulta de bloqueos puntuales del médico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero visualizar mis bloqueos registrados para revisar mis excepciones activas y poder operarlas sin ambigüedad.  
**Dependencias:** `US-S03`.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando consulta sus bloqueos, entonces recibe sus días completos y rangos horarios con fecha, motivo e identificador operativo.
2. Dado un rango de fechas, cuando el médico filtra sus bloqueos, entonces el sistema devuelve solo las excepciones comprendidas en ese período.
3. Dado un médico, cuando intenta consultar bloqueos de otro profesional, entonces el sistema no expone ese contexto.

### P5.3. US-S14 - Edición de bloqueos puntuales del médico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero editar un bloqueo existente para ajustar ausencias o contingencias sin tener que recrear manualmente toda la excepción.  
**Dependencias:** `US-S03`, `US-S13`.

**Criterios de aceptación**

1. Dado un bloqueo existente del médico, cuando actualiza fecha, rango o motivo, entonces el sistema valida solapamientos y persiste el cambio.
2. Dado una edición que coincide con citas futuras o con otro bloqueo activo, cuando se intenta guardar, entonces el sistema reporta el conflicto.
3. Dado un bloqueo perteneciente a otro profesional, cuando se intenta editar, entonces el sistema rechaza la operación.

### P5.1. US-S11 - Resumen operativo y alertas de agenda del médico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero ver un resumen consolidado de mi agenda y alertas operativas para entender rápidamente qué me falta configurar y qué restricciones impactan mi atención próxima.  
**Dependencias:** `US-S10`, `US-A02`, `US-S13`.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando consulta su resumen de agenda, entonces recibe el estado consolidado de perfil, disponibilidad, duración de consulta y agenda lista o no lista.
2. Dado feriados institucionales o bloqueos próximos, cuando el médico consulta ese resumen, entonces ve alertas claras con fecha, tipo de restricción e impacto operativo básico.
3. Dado una agenda incompleta, cuando el médico accede al resumen, entonces el sistema indica la siguiente acción requerida sin dejar estados vacíos o ambiguos.

### P6. US-S04 - Motor de generación de slots

**Actor:** Sistema  
**Descripción:** Como sistema, quiero calcular slots a partir de agenda base, feriados, bloqueos y citas existentes para ofrecer disponibilidad confiable.  
**Dependencias:** `US-S01`, `US-S02`, `US-A01`, `US-S03`.

**Criterios de aceptación**

1. Dado un médico con agenda válida, cuando se consulta un día hábil sin restricciones, entonces el sistema devuelve slots ordenados y no solapados.
2. Dado un feriado o bloqueo, cuando se consulta ese rango, entonces no se devuelve disponibilidad prohibida.
3. Dado citas existentes, cuando se genera disponibilidad, entonces se excluyen slots ocupados o incompatibles.

### P7. US-S05 - Consulta de disponibilidad para el estudiante

**Actor:** `STUDENT`  
**Descripción:** Como estudiante, quiero buscar horarios disponibles por médico o especialidad para reservar una cita que se ajuste a mi horario.  
**Dependencias:** `US-S04`.

**Criterios de aceptación**

1. Dado un estudiante autenticado, cuando consulta disponibilidad, entonces solo ve slots realmente reservables.
2. Dado un día sin disponibilidad, cuando se consulta, entonces el sistema responde lista vacía sin slots fantasma.
3. Dado un contexto inválido o ajeno, cuando se intenta forzar acceso, entonces el sistema conserva el contexto propio del usuario autenticado.

### P8. US-S06 - Reserva de cita por estudiante

**Actor:** `STUDENT`  
**Descripción:** Como estudiante, quiero reservar un slot disponible y recibir confirmación inmediata sin riesgo de doble reserva.  
**Dependencias:** `US-S05`.

**Criterios de aceptación**

1. Dado un slot disponible, cuando el estudiante reserva, entonces el sistema crea la cita con estado inicial válido.
2. Dado dos solicitudes simultáneas sobre el mismo slot, cuando compiten, entonces solo una se confirma.
3. Dado un slot ya inválido por bloqueo, cita o feriado, cuando se intenta reservar, entonces el sistema responde que el horario no está disponible.

### P9. US-S07 - Cancelación de cita por estudiante

**Actor:** `STUDENT`  
**Descripción:** Como estudiante, quiero cancelar una cita propia bajo reglas de negocio para liberar el cupo a tiempo.  
**Dependencias:** `US-S06`.

**Criterios de aceptación**

1. Dado una cita propia dentro del margen permitido, cuando el estudiante cancela, entonces la cita cambia al estado de cancelación correspondiente.
2. Dado una cita de otro paciente, cuando se intenta cancelar, entonces el sistema responde `403` o `404` según política definida.
3. Dado una cita fuera del margen permitido, cuando se intenta cancelar, entonces el sistema rechaza la operación con mensaje de negocio claro.

### P10. US-S08 - Agenda semanal y operación diaria del médico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero ver mi agenda semanal y los estados operativos de las citas para gestionar el flujo diario.  
**Dependencias:** `US-S06`.

**Criterios de aceptación**

1. Dado un médico autenticado, cuando consulta su semana, entonces recibe sus citas ordenadas cronológicamente con datos mínimos del paciente.
2. Dado un médico autenticado, cuando cambia el estado operativo de una cita, entonces la transición queda persistida y validada.
3. Dado un médico, cuando intenta ver agenda de otro profesional, entonces el sistema rechaza el acceso.

### P11. US-S09 - Reprogramación de cita por médico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero reprogramar una cita dentro de mi agenda para gestionar cambios operativos sin romper reglas de disponibilidad.  
**Dependencias:** `US-S08`, `US-S04`.

**Criterios de aceptación**

1. Dado una cita existente, cuando el médico la mueve a un slot válido, entonces la nueva hora queda confirmada.
2. Dado un destino que cae en bloqueo, feriado o conflicto, cuando intenta reprogramar, entonces el sistema rechaza la acción.
3. Dado un médico distinto al dueño de la cita, cuando intenta reprogramarla, entonces el sistema no le permite operar sobre ella.

### P11.1. US-S12 - Reconfiguración segura de agenda con impacto futuro

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero conocer el impacto de cambiar mi disponibilidad o duración de consulta cuando ya existen slots o citas futuras para no romper la operación asistencial sin advertencia.  
**Dependencias:** `US-S04`, `US-S06`, `US-S08`, `US-S09`.

**Criterios de aceptación**

1. Dado un médico con slots futuros o citas ya reservadas, cuando intenta cambiar disponibilidad base o duración de consulta, entonces el sistema evalúa y reporta el impacto antes de aplicar el cambio.
2. Dado un cambio que invalida citas confirmadas o genera conflicto operativo, cuando el médico intenta confirmarlo, entonces el sistema lo rechaza o exige una resolución explícita según regla definida.
3. Dado un cambio válido que solo afecta disponibilidad futura no reservada, cuando se aplica, entonces el sistema recalcula únicamente el horizonte futuro correspondiente sin alterar historial ni citas pasadas.

### P12. US-E01 - Regla de acceso clínico por contexto asistencial

**Actor:** Sistema  
**Descripción:** Como sistema, quiero limitar el acceso al historial clínico según relación asistencial válida para cumplir need to know.  
**Dependencias:** `US-S08`.

**Criterios de aceptación**

1. Dado un médico con relación asistencial válida, cuando consulta el EMR, entonces accede al contexto permitido.
2. Dado un médico sin vínculo asistencial válido, cuando intenta abrir un EMR ajeno, entonces el sistema niega el acceso.
3. Dado un administrador o laboratorista, cuando intenta acceder a EMR completo, entonces el sistema responde `403`.

### P13. US-E02 - Creación de ficha clínica inmutable

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero guardar una ficha clínica que no pueda modificarse después para preservar trazabilidad y validez.  
**Dependencias:** `US-E01`.

**Criterios de aceptación**

1. Dado una consulta válida, cuando el médico guarda la ficha, entonces el sistema almacena el contenido cifrado y genera su hash.
2. Dado una ficha ya creada, cuando alguien intenta editarla como registro principal, entonces el sistema no lo permite.
3. Dado una nueva ficha, cuando se guarda, entonces se invoca `BlockchainService` con la estrategia vigente.

### P14. US-E03 - Notas de corrección append-only

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero anexar correcciones a una ficha existente sin alterar el registro original.  
**Dependencias:** `US-E02`.

**Criterios de aceptación**

1. Dado una ficha clínica existente, cuando el médico registra una corrección, entonces se crea una nota separada vinculada al registro original.
2. Dado una corrección registrada, cuando se consulta el historial, entonces se visualiza junto con la ficha original manteniendo orden temporal.
3. Dado un intento de reemplazar el contenido original mediante update directo, entonces el sistema debe impedirlo.

### P15. US-E04 - Drafts y autoguardado clínico

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero que la consulta actual se autoguarde como borrador para no perder trabajo ante cierres accidentales.  
**Dependencias:** `US-E01`.

**Criterios de aceptación**

1. Dado una consulta en edición, cuando ocurre autoguardado, entonces el borrador queda asociado al médico y al paciente correctos.
2. Dado un médico distinto, cuando intenta leer un draft ajeno, entonces el sistema no lo permite.
3. Dado una ficha finalizada, cuando se consolida el registro definitivo, entonces el borrador deja de presentarse como activo.

### P16. US-E05 - Snippets clínicos

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero consultar snippets activos para acelerar redacción clínica estandarizada.  
**Dependencias:** base EMR.

**Criterios de aceptación**

1. Dado snippets activos, cuando el médico consulta la lista, entonces solo recibe macros vigentes.
2. Dado un snippet inactivo, cuando se consulta, entonces no aparece para uso clínico.
3. Dado un usuario que no es médico, cuando intenta acceder, entonces el sistema rechaza el acceso.

### P17. US-E06 - Emisión de recetas y consulta de recetas activas

**Actor:** `DOCTOR` y `STUDENT`  
**Descripción:** Como médico, quiero emitir recetas desde la consulta; como estudiante, quiero ver mis recetas activas.  
**Dependencias:** `US-E02`.

**Criterios de aceptación**

1. Dado una consulta clínica válida, cuando el médico emite una receta, entonces queda asociada al paciente y al contexto clínico.
2. Dado un estudiante autenticado, cuando consulta recetas activas, entonces solo ve las suyas.
3. Dado una receta vencida o desactivada, cuando el estudiante consulta, entonces no aparece como activa.

### P18. US-L01 - Catálogo administrable de laboratorio

**Actor:** `ADMIN`  
**Descripción:** Como administrador, quiero gestionar el catálogo de estudios para que los médicos soliciten pruebas válidas y consistentes.  
**Dependencias:** IAM actual.

**Criterios de aceptación**

1. Dado un administrador autenticado, cuando crea o actualiza un examen, entonces el catálogo queda disponible para órdenes futuras.
2. Dado un usuario sin rol `ADMIN`, cuando intenta modificar catálogo, entonces el sistema responde `403`.
3. Dado un examen inactivo, cuando un médico prepara una nueva orden, entonces no puede seleccionarlo.

### P19. US-L02 - Creación de órdenes de laboratorio desde consulta

**Actor:** `DOCTOR`  
**Descripción:** Como médico, quiero emitir órdenes de laboratorio desde la atención clínica para derivar estudios necesarios sin duplicar trabajo.  
**Dependencias:** `US-E02`, `US-L01`.

**Criterios de aceptación**

1. Dado una consulta válida, cuando el médico crea una orden, entonces el sistema registra cabecera, prioridad e items seleccionados.
2. Dado un catálogo inexistente o inactivo, cuando se intenta usar, entonces la orden se rechaza.
3. Dado un médico sin contexto clínico válido con el paciente, cuando intenta ordenar estudios, entonces el sistema niega la acción.

### P20. US-L03 - Worklist operativa para laboratorio

**Actor:** `LAB_TECH`  
**Descripción:** Como laboratorista, quiero ver una lista priorizada de órdenes pendientes para operar por urgencia y estado.  
**Dependencias:** `US-L02`.

**Criterios de aceptación**

1. Dado órdenes pendientes, cuando el laboratorista consulta la worklist, entonces la lista prioriza urgentes y muestra solo el contexto necesario.
2. Dado una orden ya cerrada, cuando se consulta la bandeja principal, entonces no aparece como pendiente.
3. Dado un administrador o estudiante, cuando intenta usar esta bandeja, entonces el sistema rechaza el acceso.

### P21. US-L04 - Carga de archivos y resultados a storage

**Actor:** `LAB_TECH`  
**Descripción:** Como laboratorista, quiero subir resultados PDF o DICOM y asociarlos a una orden para completar el procesamiento técnico.  
**Dependencias:** `US-L03`.

**Criterios de aceptación**

1. Dado un archivo válido y una orden existente, cuando se sube el resultado, entonces el archivo se almacena en MinIO y queda vinculado al item correspondiente.
2. Dado un archivo inválido, cuando se intenta subir, entonces el sistema rechaza la operación con mensaje claro.
3. Dado una orden inexistente o no autorizada, cuando se intenta adjuntar archivo, entonces la operación falla sin exponer datos extra.

### P22. US-L05 - Aprobación por lotes y publicación

**Actor:** `LAB_TECH`  
**Descripción:** Como laboratorista, quiero aprobar múltiples órdenes normales de una sola vez para acelerar la liberación de resultados.  
**Dependencias:** `US-L04`.

**Criterios de aceptación**

1. Dado varias órdenes elegibles, cuando el laboratorista ejecuta aprobación por lote, entonces todas cambian de estado en una misma operación transaccional.
2. Dado una orden no elegible dentro del lote, cuando se procesa, entonces el sistema conserva consistencia y reporta el problema.
3. Dado órdenes aprobadas, cuando el paciente o médico consultan resultados, entonces solo ven material ya publicado.

### P23. US-L06 - Descarga de resultados por médico y estudiante

**Actor:** `DOCTOR` y `STUDENT`  
**Descripción:** Como médico o estudiante autorizado, quiero descargar el resultado final publicado para consulta y seguimiento.  
**Dependencias:** `US-L05`.

**Criterios de aceptación**

1. Dado un resultado publicado, cuando el estudiante propietario lo consulta, entonces puede acceder solo a sus propios resultados.
2. Dado un médico con contexto clínico válido, cuando consulta resultados de su paciente, entonces puede descargarlos.
3. Dado un resultado aún no publicado, cuando se intenta acceder, entonces el sistema no lo expone.

### P24. US-N01 - Recordatorios de citas

**Actor:** Sistema  
**Descripción:** Como sistema, quiero emitir recordatorios de cita para reducir ausentismo.  
**Dependencias:** `US-S06`, infraestructura de notificación.

**Criterios de aceptación**

1. Dado una cita programada, cuando entra en la ventana de recordatorio, entonces se emite el evento correspondiente.
2. Dado una cita cancelada, cuando llega la ventana, entonces no se envía recordatorio.
3. Dado una falla de entrega, cuando ocurre, entonces queda trazabilidad para reintento o monitoreo.

### P25. US-B01 - Anclaje blockchain real

**Actor:** Sistema  
**Descripción:** Como sistema, quiero reemplazar `NoOpBlockchainService` por una integración real para reforzar trazabilidad e inmutabilidad verificable.  
**Dependencias:** `US-E02`, infraestructura blockchain.

**Criterios de aceptación**

1. Dado una ficha clínica confirmada, cuando se guarda, entonces el hash se ancla en la red real y retorna identificador verificable.
2. Dado una verificación posterior, cuando se consulta el hash, entonces el sistema puede validar consistencia entre base de datos y blockchain.
3. Dado una caída del servicio blockchain, cuando se produce, entonces la aplicación maneja el error según política definida sin perder trazabilidad.

## 4. Orden sugerido por iteraciones

### Iteración 1

- `US-I01`
- `US-S01`
- `US-S02`
- `US-A01`
- `US-S03`
- `US-S04`

### Iteración 2

- `US-S05`
- `US-S06`
- `US-S07`
- `US-S08`
- `US-S09`

### Iteración 3

- `US-E01`
- `US-E02`
- `US-E03`
- `US-E04`
- `US-E05`
- `US-E06`

### Iteración 4

- `US-L01`
- `US-L02`
- `US-L03`
- `US-L04`
- `US-L05`
- `US-L06`

### Iteración 5

- `US-N01`
- `US-B01`
