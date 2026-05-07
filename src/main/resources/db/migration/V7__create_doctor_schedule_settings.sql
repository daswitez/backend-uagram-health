CREATE TABLE doctor_schedule_settings (
    id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    appointment_duration_minutes INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_doctor_schedule_settings_duration
        CHECK (appointment_duration_minutes BETWEEN 10 AND 120)
);

CREATE INDEX idx_doctor_schedule_settings_doctor
    ON doctor_schedule_settings (doctor_id);
