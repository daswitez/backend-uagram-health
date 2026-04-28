-- ═══════════════════════════════════════════════════════════
-- V3: Clinical Records, Corrections, Prescriptions, Snippets
-- Module: emr
-- ═══════════════════════════════════════════════════════════

-- Core clinical record (immutable by design)
CREATE TABLE clinical_records (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    appointment_id      UUID REFERENCES appointments(id),
    patient_id          UUID NOT NULL REFERENCES users(id),
    doctor_id           UUID NOT NULL REFERENCES users(id),
    encrypted_payload   TEXT NOT NULL,
    content_hash        VARCHAR(64) NOT NULL,
    blockchain_tx_id    VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
    -- NOTE: No updated_at column. This is intentional (US-B02).
    -- Clinical records are NEVER modified. Use correction_notes instead.
);

-- Append-only correction notes
CREATE TABLE correction_notes (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    original_record_id  UUID NOT NULL REFERENCES clinical_records(id),
    doctor_id           UUID NOT NULL REFERENCES users(id),
    reason              TEXT NOT NULL,
    correction_content  TEXT NOT NULL,
    content_hash        VARCHAR(64) NOT NULL,
    blockchain_tx_id    VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Prescriptions linked to clinical encounters
CREATE TABLE prescriptions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    clinical_record_id  UUID NOT NULL REFERENCES clinical_records(id),
    patient_id          UUID NOT NULL REFERENCES users(id),
    doctor_id           UUID NOT NULL REFERENCES users(id),
    medication_name     VARCHAR(255) NOT NULL,
    dosage              VARCHAR(100) NOT NULL,
    frequency           VARCHAR(100) NOT NULL,
    duration_days       INTEGER,
    instructions        TEXT,
    start_date          DATE NOT NULL,
    end_date            DATE,
    is_active           BOOLEAN NOT NULL DEFAULT true,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Text macros for rapid clinical data entry
CREATE TABLE snippets (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trigger     VARCHAR(100) NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    category    VARCHAR(100),
    is_active   BOOLEAN NOT NULL DEFAULT true
);

CREATE INDEX idx_clinical_patient ON clinical_records(patient_id);
CREATE INDEX idx_clinical_appointment ON clinical_records(appointment_id);
CREATE INDEX idx_clinical_blockchain_tx ON clinical_records(blockchain_tx_id);
CREATE INDEX idx_correction_original ON correction_notes(original_record_id);
CREATE INDEX idx_prescription_patient ON prescriptions(patient_id);
CREATE INDEX idx_prescription_active ON prescriptions(is_active, patient_id);
CREATE INDEX idx_snippet_trigger ON snippets(trigger);
