-- ═══════════════════════════════════════════════════════════
-- Ugram Health — PostgreSQL Initialization Script
-- Runs on first container startup only.
-- ═══════════════════════════════════════════════════════════

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant privileges to the application user
GRANT ALL PRIVILEGES ON DATABASE ugram_health TO ugram;
