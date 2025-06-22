-- Use database
USE QueueManagement;

-- -------------------------------------------------------------------------------
-- ----------------------------Drop Tables First----------------------------------
-- -------------------------------------------------------------------------------
-- Drop all tables in reverse order of creation (to handle foreign key dependencies)
DROP VIEW IF EXISTS doctor_clinic_view;

DROP TABLE IF EXISTS cron_jobs;

DROP TABLE IF EXISTS queue_management;

DROP TABLE IF EXISTS appointments;

DROP TABLE IF EXISTS patients;

DROP TABLE IF EXISTS slot_generation_information;

DROP TABLE IF EXISTS slot_information;

DROP TABLE IF EXISTS doctor_absence_information;

DROP TABLE IF EXISTS doctor_information;

DROP TABLE IF EXISTS clinic_information;

DROP TABLE IF EXISTS users;

-- -------------------------------------------------------------------------------
-- -------------------------------Management Tables-------------------------------
-- -------------------------------------------------------------------------------
-- Table for clinic information
CREATE TABLE
    IF NOT EXISTS clinic_information (
        clinic_id INTEGER PRIMARY KEY AUTO_INCREMENT,
        clinic_name VARCHAR(150),
        clinic_address VARCHAR(200),
        clinic_pin_code VARCHAR(10),
        map_geo_location VARCHAR(50),
        clinic_amenities VARCHAR(200),
        clinic_email VARCHAR(120),
        clinic_timing VARCHAR(150),
        clinic_website VARCHAR(150),
        clinic_phone_numbers JSON,
        no_of_doctors INTEGER
    );

-- Table for doctor information
CREATE TABLE
    IF NOT EXISTS doctor_information (
        id INTEGER PRIMARY KEY AUTO_INCREMENT,
        doctor_id VARCHAR(50) NOT NULL,
        clinic_id INTEGER,
        doctor_name VARCHAR(120),
        gender VARCHAR(10),
        doctor_email VARCHAR(120),
        phone_numbers JSON,
        doctor_speciality VARCHAR(120),
        doctor_availability JSON,
        doctor_consultation_fee INTEGER,
        doctor_consultation_fee_other INTEGER,
        doctor_experience INTEGER,
        languages_spoken JSON,
        qualifications JSON,
        UNIQUE KEY uq_doctor_id (doctor_id),
        FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id),
        CONSTRAINT chk_consultation_fee CHECK (doctor_consultation_fee <= 1000),
        CONSTRAINT chk_doctor_experience CHECK (doctor_experience <= 70)
    );

-- Table for doctor absence information
CREATE TABLE
    IF NOT EXISTS doctor_absence_information (
        id INTEGER PRIMARY KEY AUTO_INCREMENT,
        clinic_id INTEGER,
        doctor_id VARCHAR(50),
        doctor_name VARCHAR(120),
        absence_date DATE,
        absence_start_time TIME(0),
        absence_end_time TIME(0),
        optional_message VARCHAR(255),
        FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id),
        FOREIGN KEY (doctor_id) REFERENCES doctor_information (doctor_id)
    );

-- Slot Table
CREATE TABLE
    IF NOT EXISTS slot_information (
        slot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
        slot_no INT,
        shift_time VARCHAR(255),
        slot_time TIME,
        clinic_id INT,
        doctor_id VARCHAR(255),
        slot_date DATE,
        is_available BOOLEAN
    );

-- Slot Generation Table
CREATE TABLE
    IF NOT EXISTS slot_generation_information (
        id INTEGER PRIMARY KEY AUTO_INCREMENT,
        doctor_id VARCHAR(50),
        clinic_id INTEGER,
        slot_date DATE,
        STATUS BOOLEAN,
        slots INTEGER,
        FOREIGN KEY (doctor_id) REFERENCES doctor_information (doctor_id),
        FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id)
    );

-- Table for user information
CREATE TABLE
    users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        email VARCHAR(100) NOT NULL UNIQUE,
        phone_number VARCHAR(10) NULL,
        role VARCHAR(10) NOT NULL,
        clinic_id JSON,
        is_active BOOLEAN NOT NULL DEFAULT TRUE,
        failed_login_attempts INT NOT NULL DEFAULT 0,
        locked_until TIMESTAMP NULL,
        last_login_at TIMESTAMP NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- -------------------------------------------------------------------------------
-- -------------------------------SQL Views---------------------------------------
-- -------------------------------------------------------------------------------
CREATE
OR REPLACE VIEW doctor_clinic_view AS
SELECT
    di.doctor_id,
    di.doctor_name,
    di.clinic_id,
    ci.clinic_name
FROM
    doctor_information di
    JOIN clinic_information ci ON di.clinic_id = ci.clinic_id;

-- -------------------------------------------------------------------------------
-- --------------------------------CRON JOB --------------------------------------
-- -------------------------------------------------------------------------------
CREATE TABLE
    IF NOT EXISTS cron_jobs (
        id INTEGER AUTO_INCREMENT PRIMARY KEY,
        description TEXT DEFAULT NULL,
        schedule VARCHAR(255) NOT NULL,
        enabled BOOLEAN NOT NULL DEFAULT TRUE,
        last_run DATETIME DEFAULT NULL
    );

-- SQL Insert --
INSERT INTO
    cron_jobs (description, schedule, enabled, last_run)
VALUES
    (
        'Create Queue Slots based on doctors availability',
        '0 5 18 * * *',
        1,
        NULL
    );

INSERT INTO 
    cron_job (description, schedule, enabled, last_run)
VALUES (
        'Purge Expired Refresh Tokens',
        '0 0 * * * *', 
        1,
        NULL
      );    