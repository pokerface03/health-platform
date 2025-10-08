
CREATE TABLE IF NOT EXISTS medicines (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    time TIME NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    notes TEXT
);
