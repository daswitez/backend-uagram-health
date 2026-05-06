# Mapa Funcional del Producto

## 1. Objetivo

Este documento resume las capacidades objetivo de **UAGRM Health** por actor y dominio. No reemplaza al backlog priorizado; sirve para ver el producto completo.

## 2. Actores principales

### `STUDENT`

Necesita:

- autenticarse
- ver su perfil
- reservar y cancelar citas propias
- consultar recetas activas
- consultar resultados propios

### `DOCTOR`

Necesita:

- gestionar su perfil profesional
- configurar disponibilidad y bloqueos
- ver su agenda
- atender pacientes
- escribir EMR
- emitir recetas
- crear órdenes de laboratorio

### `LAB_TECH`

Necesita:

- ver worklist de órdenes
- subir archivos y resultados
- aprobar y publicar resultados

### `ADMIN`

Necesita:

- dar de alta staff
- gestionar catálogos
- administrar feriados y calendario institucional

## 3. Dominios funcionales

### 3.1 Identity e IAM

- login y refresh
- auto-registro de estudiantes
- alta de staff
- enforcement de roles

### 3.2 Scheduling

- agenda base semanal del médico
- duración de consulta
- feriados o jornadas parciales
- bloqueos puntuales por ausencia
- generación de slots válidos
- reserva y cancelación por estudiante
- agenda semanal del médico

### 3.3 EMR

- historial cronológico
- ficha clínica inmutable
- notas de corrección append-only
- snippets o macros
- drafts/autoguardado
- recetas

### 3.4 Laboratory

- catálogo de estudios
- creación de órdenes
- worklist de laboratorio
- carga de archivos a MinIO
- aprobación masiva
- publicación de resultados
- descarga de PDF final

### 3.5 Notifications

- recordatorios de citas
- aviso de resultados
- eventos clínicos relevantes

## 4. Dependencias funcionales críticas

1. no hay reserva de citas sin agenda médica definida
2. no hay slots sin feriados, excepciones y bloqueos contemplados
3. no hay EMR seguro sin control de acceso contextual
4. no hay resultados publicados sin orden, storage y aprobación

## 5. Criterio de valor

El desarrollo debe favorecer este flujo:

1. disponibilidad médica confiable
2. reserva sin conflictos
3. atención clínica segura
4. emisión de órdenes y recetas
5. procesamiento de laboratorio
6. visibilidad final para el estudiante
