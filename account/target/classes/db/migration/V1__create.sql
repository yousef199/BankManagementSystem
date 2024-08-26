CREATE TABLE Account (
                         account_id VARCHAR(10) PRIMARY KEY,  -- 10-digit account ID starting with customer_id
                         customer_id VARCHAR(7) NOT NULL,     -- 7-digit customer ID
                         balance DECIMAL(15, 2) NOT NULL CHECK (balance >= 0),  -- Account balance, cannot be negative
                         account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('salary', 'saving', 'investment')),  -- Type of account
                         account_status VARCHAR(20) NOT NULL CHECK (account_status IN ('active', 'inactive', 'closed')) -- Status of the account
);