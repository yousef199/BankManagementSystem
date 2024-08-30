-- Create Account table in the account database
CREATE TABLE Account (
                         account_id INTEGER PRIMARY KEY,  -- 10-digit account ID
                         customer_id INTEGER NOT NULL,     -- 7-digit customer ID
                         balance DECIMAL(15, 2) NOT NULL CHECK (balance >= 0),
                         account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('salary', 'savings', 'investment')),
                         account_status VARCHAR(20) NOT NULL CHECK (account_status IN ('active', 'inactive', 'closed'))
);