-- ═══════════════════════════════════════════════════════════
-- V5: Seed Initial Data
-- Development data for testing. Remove in production.
-- ═══════════════════════════════════════════════════════════

-- Default admin user (password: "admin123" hashed with BCrypt strength 12)
INSERT INTO users (id, email, ru, password_hash, first_name, last_name, phone, user_type, is_active)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'admin@uagrm.edu.bo',
    NULL,
    '$2a$12$LJ3m4ys4C.cMVD3zFCKhVuy9FnGFH/1GBXAIkFJj3G7KlPxkSL2pu',
    'Administrador',
    'FUSUM',
    '+591 70000001',
    'ADMIN',
    true
);

-- Demo doctor (password: "doctor123")
INSERT INTO users (id, email, ru, password_hash, first_name, last_name, phone, user_type, is_active)
VALUES (
    'a0000000-0000-0000-0000-000000000002',
    'dr.martinez@uagrm.edu.bo',
    NULL,
    '$2a$12$LJ3m4ys4C.cMVD3zFCKhVuy9FnGFH/1GBXAIkFJj3G7KlPxkSL2pu',
    'Carlos',
    'Martínez',
    '+591 70000002',
    'DOCTOR',
    true
);

INSERT INTO doctors (user_id, medical_license, specialty)
VALUES ('a0000000-0000-0000-0000-000000000002', 'MP-12345', 'Medicina General');

-- Demo student (password: "student123")
INSERT INTO users (id, email, ru, password_hash, first_name, last_name, phone, user_type, is_active)
VALUES (
    'a0000000-0000-0000-0000-000000000003',
    'juan.perez@est.uagrm.edu.bo',
    '220012345',
    '$2a$12$LJ3m4ys4C.cMVD3zFCKhVuy9FnGFH/1GBXAIkFJj3G7KlPxkSL2pu',
    'Juan',
    'Pérez',
    '+591 70000003',
    'STUDENT',
    true
);

INSERT INTO patients (user_id, career, blood_type, date_of_birth)
VALUES ('a0000000-0000-0000-0000-000000000003', 'Ingeniería en Sistemas', 'O+', '2002-05-15');

-- Demo lab tech (password: "lab123")
INSERT INTO users (id, email, ru, password_hash, first_name, last_name, phone, user_type, is_active)
VALUES (
    'a0000000-0000-0000-0000-000000000004',
    'lab.garcia@uagrm.edu.bo',
    NULL,
    '$2a$12$LJ3m4ys4C.cMVD3zFCKhVuy9FnGFH/1GBXAIkFJj3G7KlPxkSL2pu',
    'María',
    'García',
    '+591 70000004',
    'LAB_TECH',
    true
);

-- ── Lab Catalog Seed ─────────────────────────────────────
INSERT INTO lab_catalogs (group_name, test_name, turnaround_time_desc, reference_range, unit) VALUES
('Hematología', 'Hemograma Completo (CBC)', '2-4 horas', '4.5-11.0', '×10³/µL'),
('Hematología', 'Velocidad de Sedimentación (VSG)', '1 hora', '0-20', 'mm/h'),
('Bioquímica', 'Glucosa en Ayunas', '2 horas', '70-100', 'mg/dL'),
('Bioquímica', 'Colesterol Total', '2 horas', '<200', 'mg/dL'),
('Bioquímica', 'Triglicéridos', '2 horas', '<150', 'mg/dL'),
('Bioquímica', 'Creatinina', '2 horas', '0.7-1.3', 'mg/dL'),
('Uroanálisis', 'Examen General de Orina', '1-2 horas', 'N/A', ''),
('Serología', 'PCR (Proteína C Reactiva)', '4 horas', '<6', 'mg/L'),
('Imagenología', 'Radiografía de Tórax', '30 min', 'N/A', '');

-- ── Snippets Seed ────────────────────────────────────────
INSERT INTO snippets (trigger, name, content, category) VALUES
('gripe', 'Tratamiento Gripe Estándar', 'Diagnóstico: Infección respiratoria aguda viral (J06.9)\n\nPlan terapéutico:\n1. Paracetamol 500mg VO c/8h por 5 días\n2. Hidratación oral abundante (>2L/día)\n3. Reposo relativo por 3-5 días\n4. Control en 7 días si persisten síntomas\n\nSignos de alarma: fiebre >39°C persistente, dificultad respiratoria, dolor torácico.', 'tratamiento'),
('cefalea', 'Evaluación Cefalea', 'Anamnesis cefalea:\n- Localización: \n- Intensidad (EVA 1-10): \n- Duración: \n- Frecuencia: \n- Factores desencadenantes: \n- Síntomas asociados: náusea/vómito/fotofobia/fonofobia\n- Medicación previa: \n\nExamen neurológico básico:\n- Signos meníngeos: negativos\n- Pares craneales: normales\n- Fondo de ojo: pendiente', 'evaluación'),
('fractura_simple', 'Manejo Fractura Simple', 'Diagnóstico: Fractura cerrada no desplazada\n\nPlan:\n1. Inmovilización con férula/yeso\n2. Ibuprofeno 400mg VO c/8h por 7 días (con alimento)\n3. Elevación del miembro afectado\n4. Crioterapia local 20min c/4h primeras 48h\n5. Radiografía de control en 7-10 días\n6. Referencia a traumatología si desplazamiento', 'tratamiento');
