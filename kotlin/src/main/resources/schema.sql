-- Matches README.md Requirement 2's `timeDeposits` table definition.
CREATE TABLE IF NOT EXISTS time_deposits (
    id INTEGER PRIMARY KEY,
    plan_type VARCHAR(255) NOT NULL,
    days INTEGER NOT NULL,
    balance DECIMAL(19, 2) NOT NULL
);
