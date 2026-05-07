CREATE TABLE institutional_holidays (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    type VARCHAR(20) NOT NULL,
    start_time TIME,
    end_time TIME,
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_institutional_holiday_type
        CHECK (type IN ('TOTAL', 'PARTIAL')),
    CONSTRAINT chk_institutional_holiday_time_window
        CHECK (
            (type = 'TOTAL' AND start_time IS NULL AND end_time IS NULL)
            OR
            (type = 'PARTIAL' AND start_time IS NOT NULL AND end_time IS NOT NULL AND end_time > start_time)
        )
);

CREATE INDEX idx_institutional_holidays_date
    ON institutional_holidays (date);
