
CREATE EXTENSION IF NOT EXISTS timescaledb;


CREATE TABLE IF NOT EXISTS vitals (
    id SERIAL,
    user_id VARCHAR(255) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    metric_name VARCHAR(50) NOT NULL,
    value DOUBLE PRECISION NOT NULL
);


SELECT create_hypertable('vitals', 'timestamp', if_not_exists => TRUE);


ALTER TABLE vitals ADD CONSTRAINT vitals_pkey
    PRIMARY KEY (timestamp, id);
