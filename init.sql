DROP TABLE IF EXISTS clinic_information;
DROP TABLE IF EXISTS doctor_information;
DROP TABLE IF EXISTS doctor_absence_information;
DROP TABLE IF EXISTS slot_information;
DROP TABLE IF EXISTS slot_generation_information;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS cron_jobs;
DROP TABLE IF EXISTS queue_management;

-- Table for clinic information
CREATE TABLE IF NOT EXISTS clinic_information (
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
CREATE TABLE IF NOT EXISTS doctor_information (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    doctor_id VARCHAR(50) UNIQUE,
    clinic_id INTEGER,
    doctor_name VARCHAR(120),
    phone_numbers JSON,
    doctor_speciality VARCHAR(120),
    doctor_availability JSON,
    doctor_consultation_fee INTEGER CHECK (doctor_consultation_fee <= 1000),
    doctor_consultation_fee_other INTEGER,
    doctor_experience INTEGER CHECK (doctor_experience <= 70),
    FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id)
);

-- Table for doctor absence information
CREATE TABLE IF NOT EXISTS doctor_absence_information (
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
CREATE TABLE IF NOT EXISTS slot_information (
 slot_id INTEGER PRIMARY KEY AUTO_INCREMENT,
 slot_no INT,
 shift_time VARCHAR(50),
 slot_time VARCHAR(50),
 clinic_id INTEGER,
 doctor_id VARCHAR(50),
 slot_date VARCHAR(50),
 is_available BOOLEAN
);

-- Slot Generation Table
CREATE TABLE IF NOT EXISTS slot_generation_information (
 id INTEGER PRIMARY KEY AUTO_INCREMENT,
 doctor_id VARCHAR(50),
 clinic_id INTEGER,
 slot_date DATE, STATUS BOOLEAN,
 slots INTEGER
);

-- User Registration Table
CREATE TABLE IF NOT EXISTS users (
 id INTEGER PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(100),
 phoneNumber VARCHAR(13) UNIQUE,
 email VARCHAR(100),
 birthdate DATE
);

-- Appointment Registration Table
CREATE TABLE IF NOT EXISTS appointments (
 appointment_id INTEGER PRIMARY KEY AUTO_INCREMENT,
 user_id INT NOT NULL,
 appointment_type VARCHAR(50) NOT NULL,
 appointment_for VARCHAR(10) NOT NULL,
 appointment_for_name VARCHAR(255) NOT NULL,
 appointment_for_age INT,
 symptom VARCHAR(255),
 other_symptoms VARCHAR(255),
 appointment_date DATETIME NOT NULL,
 slot_id INTEGER,
 doctor_id VARCHAR(50) NOT NULL,
 clinic_id INTEGER NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE, UNIQUE INDEX slot_id (slot_id) USING BTREE, CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES QueueManagement.users (id), CONSTRAINT doctor_fk FOREIGN KEY (doctor_id) REFERENCES QueueManagement.doctor_information (doctor_id), CONSTRAINT clinic_fk FOREIGN KEY (clinic_id) REFERENCES QueueManagement.clinic_information (clinic_id),
 KEY doctor_id_idx (doctor_id)
);

-- Cron Jobs Table
CREATE TABLE IF NOT EXISTS cron_jobs (
 id INTEGER AUTO_INCREMENT PRIMARY KEY,
 description TEXT DEFAULT NULL, SCHEDULE VARCHAR(255) NOT NULL,
 enabled BOOLEAN NOT NULL DEFAULT TRUE,
 last_run DATETIME DEFAULT NULL
);

-- Queue Management Table
CREATE TABLE IF NOT EXISTS queue_management (
 queue_management_id INTEGER AUTO_INCREMENT PRIMARY KEY,
 appointment_id INTEGER,
 slot_id INTEGER,
 clinic_id INTEGER,
 doctor_id VARCHAR(50),
 initial_queue_no INT,
 current_queue_no INT,
 advance_paid BOOLEAN,
 cancelled BOOLEAN,
 deleted BOOLEAN DEFAULT FALSE,
 advance_revert_if_paid BOOLEAN,
 patient_reached BOOLEAN,
 visit_status VARCHAR(255),
 consultation_fee_paid BOOLEAN,
 consultation_fee_amount DECIMAL(10, 2),
 transaction_id_advance_fee VARCHAR(255),
 transaction_id_consultation_fee VARCHAR(255),
 transaction_id_advance_revert VARCHAR(255),
 queue_date DATE, FOREIGN KEY (slot_id) REFERENCES slot_information (slot_id), FOREIGN KEY (appointment_id) REFERENCES QueueManagement.appointments (appointment_id)
);

-- SQL Views --
CREATE VIEW doctor_clinic_view AS
SELECT
    di.doctor_id,
    di.doctor_name,
    di.clinic_id,
    ci.clinic_name
FROM
    doctor_information di
JOIN
    clinic_information ci
ON
    di.clinic_id = ci.clinic_id;

-- SQL Insert --
INSERT INTO cron_jobs (description, schedule, enabled, last_run)
VALUES ('Create Queue Slots based on doctors availability', '0 5 18 * * *', 1, NULL);