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
CREATE TABLE
 IF NOT EXISTS clinic_information (
 clinic_id INTEGER NOT NULL AUTO_INCREMENT,
 clinic_name VARCHAR(150),
 clinic_address VARCHAR(200),
 clinic_pin_code VARCHAR(10),
 map_geo_location VARCHAR(50),
 clinic_amenities VARCHAR(200),
 clinic_email VARCHAR(120),
 clinic_timing VARCHAR(150),
 clinic_website VARCHAR(150),
 clinic_phone_numbers JSON,
 no_of_doctors INTEGER, PRIMARY KEY (clinic_id)
) ENGINE = InnoDB;

-- Table for doctor information
CREATE TABLE IF NOT EXISTS doctor_information (
    id INTEGER NOT NULL AUTO_INCREMENT,
    doctor_id VARCHAR(50),
    clinic_id INTEGER,
    doctor_name VARCHAR(120),
    phone_numbers JSON,
    doctor_speciality VARCHAR(120),
    doctor_availability JSON,
    doctor_consultation_fee INTEGER CHECK (doctor_consultation_fee <= 1000),
    doctor_consultation_fee_other INTEGER,
    doctor_experience INTEGER CHECK (doctor_experience <= 70),
    PRIMARY KEY (id),
    FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id),
    UNIQUE (doctor_id),  -- Make doctor_id a unique key
    KEY doctor_id_idx (doctor_id)
) ENGINE = InnoDB;

-- Table for doctor absence information
CREATE TABLE IF NOT EXISTS doctor_absence_information (
    id INTEGER NOT NULL AUTO_INCREMENT,
    clinic_id INTEGER,
    doctor_id VARCHAR(50),
    doctor_name VARCHAR(120),
    absence_date DATE,
    absence_start_time TIME(0),
    absence_end_time TIME(0),
    optional_message VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id),
    FOREIGN KEY (doctor_id) REFERENCES doctor_information (doctor_id)
) ENGINE = InnoDB;


-- Slot Table
CREATE TABLE
 IF NOT EXISTS slot_information (
 slot_id INTEGER AUTO_INCREMENT PRIMARY KEY,
 slot_no INT,
 shift_time VARCHAR(50),
 slot_time VARCHAR(50),
 clinic_id INTEGER,
 doctor_id VARCHAR(50),
 slot_date VARCHAR(50),
 is_available BOOLEAN
);

-- Slot Generation Table
CREATE TABLE
 IF NOT EXISTS slot_generation_information (
 id INT AUTO_INCREMENT PRIMARY KEY,
 doctor_id VARCHAR(50),
 clinic_id INTEGER,
 slot_date DATE, STATUS BOOLEAN,
 slots INTEGER
);

-- User Registration Table
CREATE TABLE
 IF NOT EXISTS users (
 id INTEGER AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(100),
 phoneNumber VARCHAR(13) UNIQUE,
 email VARCHAR(100),
 birthdate DATE
);

-- Appointment Registration Table
CREATE TABLE
 IF NOT EXISTS appointments (
 appointment_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
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
 active BOOLEAN NOT NULL DEFAULT TRUE, UNIQUE INDEX `slot_id` (`slot_id`) USING BTREE, CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES users (id), CONSTRAINT doctor_fk FOREIGN KEY (doctor_id) REFERENCES doctor_information (doctor_id), CONSTRAINT clinic_fk FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id),
 -- Add the missing index
KEY doctor_id_idx (doctor_id)
);
CREATE TABLE
 IF NOT EXISTS cron_jobs (
 id INTEGER AUTO_INCREMENT PRIMARY KEY,
 description TEXT DEFAULT NULL, SCHEDULE VARCHAR(255) NOT NULL,
 enabled BOOLEAN NOT NULL DEFAULT TRUE,
 last_run DATETIME DEFAULT NULL
);
CREATE TABLE
 IF NOT EXISTS queue_management (
 queue_management_id INTEGER AUTO_INCREMENT PRIMARY KEY,
 appointment_id INTEGER,
 slot_id INTEGER,
 clinic_id INTEGER,
 doctor_id VARCHAR(50),
 initial_queue_no INT,
 current_queue_no INT,
 advance_paid BOOLEAN,
 cancelled BOOLEAN,
 advance_revert_if_paid BOOLEAN,
 patient_reached BOOLEAN,
 visit_status VARCHAR(255),
 consultation_fee_paid BOOLEAN,
 consultation_fee_amount DECIMAL(10, 2),
 transaction_id_advance_fee VARCHAR(255),
 transaction_id_consultation_fee VARCHAR(255),
 transaction_id_advance_revert VARCHAR(255),
 queue_date DATE, FOREIGN KEY (slot_id) REFERENCES slot_information (slot_id), FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id)
);

-- SQL Views --
CREATE VIEW doctor_clinic_view AS
SELECT di.doctor_id, di.doctor_name, di.clinic_id, ci.clinic_name
FROM doctor_information di
JOIN clinic_information ci ON di.clinic_id = ci.clinic_id;

-- SQL Inserts --
INSERT INTO
 `clinic_information` (
 `clinic_name`,
 `clinic_address`,
 `clinic_pin_code`,
 `map_geo_location`,
 `clinic_amenities`,
 `clinic_email`,
 `clinic_timing`,
 `clinic_website`,
 `clinic_phone_numbers`,
 `no_of_doctors`
) VALUES
 (
 'Sample Clinic',
 'Sample Address',
 '600103',
 '40.7128,-74.006',
 'Wifi, TV',
 'testclinic@test.com',
 'MON - FRI  09:00 - 21:00, SAT & SUN 18:00 - 21:00',
 'https://drdeepakclinic.com',
 '[
    {
      "phoneNumber": "+919789801844"
    }
  ]',
 1
);
INSERT INTO
 `doctor_information` (
 `doctor_id`,
 `clinic_id`,
 `doctor_name`,
 `phone_numbers`,
 `doctor_speciality`,
 `doctor_availability`,
 `doctor_consultation_fee`,
 `doctor_consultation_fee_other`,
 `doctor_experience`
) VALUES
 (
 'AB00001',
 1,
 'Dr. Deepak Sekarbabu',
 '[
  {
    "phoneNumber": "+91 8932154652"
  }
]',
 'Dental Specialist',
 '[
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "MONDAY",
    "shiftStartTime": "09:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "MONDAY",
    "shiftStartTime": "18:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "TUESDAY",
    "shiftStartTime": "09:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "TUESDAY",
    "shiftStartTime": "18:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "WEDNESDAY",
    "shiftStartTime": "09:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "WEDNESDAY",
    "shiftStartTime": "18:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "THURSDAY",
    "shiftStartTime": "09:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "THURSDAY",
    "shiftStartTime": "18:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "FRIDAY",
    "shiftStartTime": "09:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "FRIDAY",
    "shiftStartTime": "18:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "SATURDAY",
    "shiftStartTime": "18:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "12:00:00",
    "availableDays": "SUNDAY",
    "shiftStartTime": "10:30:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "SUNDAY",
    "shiftStartTime": "18:00:00",
    "consultationTime": 10,
    "configType": "APPOINTMENT"
  }
]',
 500,
 300,
 12
);
INSERT INTO
 `doctor_absence_information` (
 `clinic_id`,
 `doctor_id`,
 `doctor_name`,
 `absence_date`,
 `absence_start_time`,
 `absence_end_time`,
 `optional_message`
) VALUES
 (
 1,
 'AB00001',
 'Dr. Deepak Sekarbabu', CURDATE(),
 '10:00:00',
 '11:00:00',
 'Personal Emergency'
);
INSERT INTO
 `doctor_absence_information` (
 `clinic_id`,
 `doctor_id`,
 `doctor_name`,
 `absence_date`,
 `absence_end_time`,
 `absence_start_time`,
 `optional_message`
) VALUES
 (
 1,
 'AB00001',
 'Dr. Deepak Sekarbabu', CURDATE(),
 '19:00:00',
 '18:00:00',
 'Personal Emergency'
);
INSERT INTO
 users (name, phoneNumber, email, birthdate) VALUES
 (
 'Deepak S',
 '+919789801844',
 'deepak@example.com',
 '1990-01-01'
),
 (
 'Jane Doe',
 '+919789801843',
 'janedoe@example.com',
 '1995-02-15'
),
 (
 'Alice Smith',
 '+919789801842',
 'alicesmith@example.com',
 '2000-10-24'
);
INSERT INTO
 appointments (
 user_id,
 appointment_type,
 appointment_for,
 appointment_for_name,
 appointment_for_age,
 symptom,
 other_symptoms,
 appointment_date,
 doctor_id,
 clinic_id
) VALUES
 (
 1,
 'GENERAL_CHECKUP',
 'SELF',
 'Deepak S',
 30,
 'HEADACHE',
 'Nausea',
 '2024-02-20 10:00:00',
 'AB00001',
 1
);
INSERT INTO
 cron_jobs (description, SCHEDULE, enabled, last_run) VALUES
 (
 'Create Queue Slots based on doctors availability',
 '0 5 18 * * *',
 1, NULL
);
