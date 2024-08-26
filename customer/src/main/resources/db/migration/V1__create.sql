-- V1__Create_customer_table.sql
CREATE TABLE Customer (
                          customer_id VARCHAR(7) PRIMARY KEY,   -- 7-digit customer ID
                          name VARCHAR(100) NOT NULL,           -- Customer's name
                          legal_id VARCHAR(50) NOT NULL UNIQUE, -- Unique legal identifier (e.g., SSN, Tax ID)
                          type VARCHAR(20) NOT NULL CHECK (type IN ('retail', 'corporate', 'investment')),  -- Type of customer
                          address VARCHAR(255) NOT NULL,        -- Address of the customer
                          phone_number VARCHAR(15),             -- Optional phone number
                          email VARCHAR(150)                    -- Optional email, must be unique if provided
);
