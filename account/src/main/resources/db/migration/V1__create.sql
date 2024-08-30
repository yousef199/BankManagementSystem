-- Create Account table in the account database
CREATE TABLE Account (
                         account_id VARCHAR(10) PRIMARY KEY,  -- 10-digit account ID
                         customer_id VARCHAR(7) NOT NULL,     -- 7-digit customer ID
                         balance DECIMAL(15, 2) NOT NULL CHECK (balance >= 0),
                         account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('salary', 'saving', 'investment')),
                         account_status VARCHAR(20) NOT NULL CHECK (account_status IN ('active', 'inactive', 'closed')),
                         CONSTRAINT chk_account_id CHECK (LEFT(account_id, 7) = customer_id)
);

-- Create a sequence for generating the account_id suffix
CREATE SEQUENCE account_suffix_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 999
    CYCLE;

-- Create a trigger function to generate account_id
CREATE OR REPLACE FUNCTION generate_account_id()
    RETURNS TRIGGER AS $$
DECLARE
    next_suffix VARCHAR(3);
BEGIN
    -- Get the next value from the sequence and format it as a 3-digit string
    next_suffix := LPAD((nextval('account_suffix_seq') % 1000)::TEXT, 3, '0');

    -- Concatenate customer_id with the 3-digit suffix to form account_id
    NEW.account_id := NEW.customer_id || next_suffix;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create a trigger to execute the function before inserting a new record
CREATE TRIGGER before_insert_account
    BEFORE INSERT ON Account
    FOR EACH ROW
EXECUTE FUNCTION generate_account_id();