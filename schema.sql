-- Tutor Connect database schema with a few sample tutors.
-- Run once:  psql -d tutor_db -f schema.sql

CREATE TABLE IF NOT EXISTS users (
    id       SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    budget   NUMERIC(10, 2) NOT NULL DEFAULT 100 CHECK (budget >= 0)
);

CREATE TABLE IF NOT EXISTS tutors (
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    age   INT,
    price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS tutor_languages (
    tutor_id INT REFERENCES tutors (id) ON DELETE CASCADE,
    language VARCHAR(50) NOT NULL,
    level    INT NOT NULL
);

CREATE TABLE IF NOT EXISTS appointments (
    id           SERIAL PRIMARY KEY,
    tutor_id     INT REFERENCES tutors (id),
    student_name VARCHAR(100) NOT NULL,
    meeting_date VARCHAR(20) NOT NULL,
    duration     INT NOT NULL,
    total_cost   NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    id           SERIAL PRIMARY KEY,
    tutor_id     INT,
    rating       INT,
    comment      TEXT,
    student_name VARCHAR(100)
);

INSERT INTO tutors (name, age, price) VALUES
    ('Andrei Pop', 24, 50),
    ('Maria Ionescu', 31, 70),
    ('Elena Varga', 27, 60);

INSERT INTO tutor_languages (tutor_id, language, level) VALUES
    (1, 'English', 5), (1, 'German', 3),
    (2, 'French', 5), (2, 'English', 4),
    (3, 'Hungarian', 5), (3, 'Romanian', 5);
