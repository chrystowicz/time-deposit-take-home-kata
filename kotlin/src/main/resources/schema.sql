-- Matches README.md Requirement 2's `timeDeposits` table definition.
CREATE TABLE IF NOT EXISTS time_deposits (
    id INTEGER PRIMARY KEY,
    plan_type VARCHAR(255) NOT NULL,
    days INTEGER NOT NULL,
    balance DECIMAL(19, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS withdrawals (
    id INTEGER PRIMARY KEY,
    time_deposit_id INTEGER NOT NULL REFERENCES time_deposits (id),
    amount DECIMAL(19, 2) NOT NULL,
    date DATE NOT NULL
);
