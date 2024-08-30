-- Create a sequence for auto-generating customer IDs as 7-digit strings
CREATE SEQUENCE customer_id_seq
    START WITH 1000000  -- Start from 1000000 to ensure 7 digits
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create the Customer table with an auto-generated 7-digit customer_id
CREATE TABLE Customer (
                          customer_id VARCHAR(7) PRIMARY KEY DEFAULT LPAD(nextval('customer_id_seq')::text, 7, '0'), -- Auto-generated 7-digit customer ID
                          name VARCHAR(100) NOT NULL,           -- Customer's name
                          legal_id VARCHAR(50) NOT NULL UNIQUE, -- Unique legal identifier (e.g., SSN, Tax ID)
                          type VARCHAR(20) NOT NULL CHECK (type IN ('retail', 'corporate', 'investment')),  -- Type of customer
                          address VARCHAR(255) NOT NULL,        -- Address of the customer
                          phone_number VARCHAR(15),             -- Optional phone number
                          email VARCHAR(150) UNIQUE             -- Optional email, must be unique if provided
);
