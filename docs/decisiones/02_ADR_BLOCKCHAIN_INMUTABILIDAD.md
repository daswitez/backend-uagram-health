# ADR: Inmutabilidad EMR y Blockchain (Diseño por Fases)

**Fecha:** Abril 2026
**Estado:** Aceptado

## Contexto

La historia clínica (EMR) contiene información sensible. Para proteger legalmente al personal médico de FUSUM y dar garantías a los estudiantes, el historial debe ser inmutable y auditable mediante Blockchain (Hyperledger Fabric). Sin embargo, configurar y mantener una red Hyperledger toma tiempo y podría bloquear el desarrollo del resto de funcionalidades (Frontend, App Móvil, Agendamiento).

## Decisión

Se implementará un modelo de **Inmutabilidad Híbrida mediante Interfaces**, dividiendo el desarrollo en fases.

### Diseño de la Inmutabilidad en Base de Datos
*   La tabla `clinical_records` solo se puede insertar (`INSERT`), nunca actualizar (`UPDATE`). No existe el campo `updated_at`.
*   El contenido clínico (diagnóstico, síntomas) se cifra simétricamente con AES-GCM antes de guardarse en la columna `encrypted_payload`.
*   Se genera un Hash criptográfico (`SHA-256`) del JSON original (sin cifrar) y se guarda en la columna `content_hash`.

### Diseño de Interfaz Blockchain (`BlockchainService`)
Se definió la interfaz `BlockchainService` en el módulo `emr`.

1. **Fase 1 a 3 (Actualidad): `NoOpBlockchainService`**
   Esta implementación por defecto (inyectada por Spring) simula el anclaje a la blockchain. Imprime el Hash en la consola y devuelve un "Transaction ID" falso (`noop-tx-...`). 
   *Consecuencia:* Permite al equipo de Frontend y Backend desarrollar, testear y terminar el 100% del portal clínico sin depender de la infraestructura Blockchain.

2. **Fase 4 (Futuro): `HyperledgerBlockchainService`**
   Se creará esta implementación que usará el *Fabric Gateway SDK* en Java para enviar el Hash al Chaincode real.
   *Consecuencia:* El cambio será totalmente transparente para los controladores y servicios del EMR. Solo cambiará qué clase inyecta Spring Boot.

## Notas de Corrección
Si un médico se equivoca al redactar una ficha, el sistema NO altera la tabla original. El médico emite una "Nota de Corrección" (tabla `correction_notes`). Esto es una práctica estándar en auditorías médicas (append-only ledger).
