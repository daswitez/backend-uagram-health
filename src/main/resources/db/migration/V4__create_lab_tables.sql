-- ═══════════════════════════════════════════════════════════
-- V4: Laboratory Module
-- Module: laboratory
-- ═══════════════════════════════════════════════════════════

-- Master catalog of available lab tests
CREATE TABLE lab_catalogs (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_name              VARCHAR(200) NOT NULL,
    test_name               VARCHAR(200) NOT NULL,
    turnaround_time_desc    VARCHAR(100),
    reference_range         VARCHAR(200),
    unit                    VARCHAR(50),
    is_active               BOOLEAN NOT NULL DEFAULT true
);

-- Lab order header
CREATE TABLE lab_orders (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id      UUID NOT NULL REFERENCES users(id),
    ordered_by      UUID NOT NULL REFERENCES users(id),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority        VARCHAR(10) NOT NULL DEFAULT 'ROUTINE',
    clinical_notes  TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- Individual test items within an order
CREATE TABLE lab_order_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL REFERENCES lab_orders(id) ON DELETE CASCADE,
    catalog_id      UUID NOT NULL REFERENCES lab_catalogs(id),
    result_value    VARCHAR(255),
    result_flag     VARCHAR(10),
    file_url        VARCHAR(500)
);

CREATE INDEX idx_lab_order_status ON lab_orders(status);
CREATE INDEX idx_lab_order_patient ON lab_orders(patient_id);
CREATE INDEX idx_lab_order_priority ON lab_orders(priority);
CREATE INDEX idx_lab_order_item_order ON lab_order_items(order_id);
