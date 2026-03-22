CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    event_date TIMESTAMP(6) NOT NULL,
    location VARCHAR(255) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity >= 1)
    );

CREATE INDEX IF NOT EXISTS idx_events_location ON events (location);
CREATE INDEX IF NOT EXISTS idx_events_event_date ON events (event_date);

CREATE TABLE IF NOT EXISTS event_registration (
    event_id BIGINT NOT NULL,
    student_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (event_id, student_id),
    CONSTRAINT fk_event_registration_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
    );