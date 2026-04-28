-- ═══════════════════════════════════════════════════════════
-- V1: Users, Roles, and Identity
-- Module: identity
-- ═══════════════════════════════════════════════════════════

CREATE TYPE user_type AS ENUM ('STUDENT', 'DOCTOR', 'ADMIN', 'LAB_TECH', 'RECEPTIONIST');

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    ru              VARCHAR(50) UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20),
    user_type       VARCHAR(20) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- Extended profile for students
CREATE TABLE patients (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL UNIQUE REFERENCES users(id),
    career          VARCHAR(200),
    blood_type      VARCHAR(5),
    date_of_birth   DATE,
    allergies       TEXT
);

-- Extended profile for doctors
CREATE TABLE doctors (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL UNIQUE REFERENCES users(id),
    medical_license VARCHAR(100) NOT NULL,
    specialty       VARCHAR(200) NOT NULL
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_ru ON users(ru);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_patients_user ON patients(user_id);
CREATE INDEX idx_doctors_user ON doctors(user_id);
