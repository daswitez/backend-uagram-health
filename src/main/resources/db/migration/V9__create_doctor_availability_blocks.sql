CREATE TABLE doctor_availability_blocks (
    id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    is_all_day BOOLEAN NOT NULL DEFAULT FALSE,
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_doctor_availability_block_range CHECK (end_at > start_at)
);

CREATE INDEX idx_doctor_availability_blocks_doctor_start
    ON doctor_availability_blocks (doctor_id, start_at);
