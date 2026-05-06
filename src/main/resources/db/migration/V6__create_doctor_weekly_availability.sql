-- ═══════════════════════════════════════════════════════════
-- V6: Doctor Weekly Availability
-- Module: scheduling
-- ═══════════════════════════════════════════════════════════

CREATE TABLE doctor_weekly_availability (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    doctor_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    day_of_week     VARCHAR(20) NOT NULL,
    start_time      TIME NOT NULL,
    end_time        TIME NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW(),

    CONSTRAINT chk_doctor_weekly_availability_times CHECK (end_time > start_time)
);

CREATE INDEX idx_doctor_weekly_availability_doctor
    ON doctor_weekly_availability(doctor_id);

CREATE INDEX idx_doctor_weekly_availability_day
    ON doctor_weekly_availability(doctor_id, day_of_week);
