-- ═══════════════════════════════════════════════════════════
-- V2: Appointments (Scheduling Module)
-- Module: scheduling
-- ═══════════════════════════════════════════════════════════

CREATE TABLE appointments (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id          UUID NOT NULL REFERENCES users(id),
    doctor_id           UUID NOT NULL REFERENCES users(id),
    scheduled_start     TIMESTAMP NOT NULL,
    scheduled_end       TIMESTAMP NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    type                VARCHAR(20) NOT NULL DEFAULT 'ROUTINE',
    cancellation_reason TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT chk_appointment_times CHECK (scheduled_end > scheduled_start)
);

CREATE INDEX idx_appointment_doctor_date ON appointments(doctor_id, scheduled_start);
CREATE INDEX idx_appointment_patient ON appointments(patient_id);
CREATE INDEX idx_appointment_status ON appointments(status);
