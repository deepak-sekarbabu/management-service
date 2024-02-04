DROP TABLE IF EXISTS clinic_information;
DROP TABLE IF EXISTS doctor_absence_information;
DROP TABLE IF EXISTS doctor_information;

-- Table for clinic information
CREATE TABLE IF NOT EXISTS clinic_information
(
    clinic_id            INTEGER NOT NULL AUTO_INCREMENT,
    clinic_name          VARCHAR(150),
    clinic_address       VARCHAR(200),
    clinic_pin_code      VARCHAR(10),
    map_geo_location     VARCHAR(50),
    clinic_amenities     VARCHAR(200),
    clinic_email         VARCHAR(120),
    clinic_timing        VARCHAR(150),
    clinic_website       VARCHAR(150),
    clinic_phone_numbers JSON,
    no_of_doctors        INTEGER CHECK ((no_of_doctors <= 100) AND (no_of_doctors >= 1)),
    PRIMARY KEY (clinic_id)
) ENGINE = InnoDB;

-- Table for doctor information
CREATE TABLE IF NOT EXISTS doctor_information
(
    PRIMARY KEY (id),
    doctor_id                VARCHAR(255),
    clinic_id                INTEGER,
    doctor_name              VARCHAR(120),
    phone_numbers            JSON,
    doctor_speciality        VARCHAR(120),
    doctor_availability      JSON,
    doctor_consultation_fee  INTEGER CHECK (doctor_consultation_fee <= 1000),
    doctor_consultation_time INTEGER,
    doctor_experience        INTEGER CHECK (doctor_experience <= 70),
    id                       INTEGER NOT NULL AUTO_INCREMENT,
    FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id) -- Foreign key relation to clinic_information
) ENGINE = InnoDB;
-- Table for doctor absence information
CREATE TABLE IF NOT EXISTS doctor_absence_information
(
    id                 INTEGER NOT NULL AUTO_INCREMENT,
    clinic_id          INTEGER,
    doctor_id          VARCHAR(255),
    doctor_name        VARCHAR(120),
    absence_date       DATE,
    absence_end_time   TIME(0),
    absence_start_time TIME(0),
    optional_message   VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id) -- Foreign key relation to clinic_information
) ENGINE = InnoDB;

-- SQL Inserts --

INSERT INTO `clinic_information` (`clinic_name`, `clinic_address`, `clinic_pin_code`, `map_geo_location`,
                                  `clinic_amenities`, `clinic_email`, `clinic_timing`, `clinic_website`,
                                  `clinic_phone_numbers`, `no_of_doctors`)
VALUES ('Sample Clinic', 'Sample Address', '600103', '40.7128,-74.006', NULL, 'testclinic@test.com',
        'MON - FRI  09:00 - 21:00, SAT & SUN 18:00 - 21:00', 'https://drdeepakclinic.com', '[
    {
      "phoneNumber": "+919789801844"
    }
  ]', 1);

INSERT INTO `doctor_information` (`doctor_id`, `clinic_id`, `doctor_name`, `phone_numbers`, `doctor_speciality`,
                                  `doctor_availability`, `doctor_consultation_fee`, `doctor_consultation_time`,
                                  `doctor_experience`)
VALUES ('AB00001', 1, 'Dr. Deepak Sekarbabu', '[
  {
    "phoneNumber": "+91 8932154652"
  }
]', 'Dental Specialist', '[
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "MONDAY",
    "shiftStartTime": "09:00:00"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "MONDAY",
    "shiftStartTime": "18:00:00"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "TUESDAY",
    "shiftStartTime": "09:00:00"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "TUESDAY",
    "shiftStartTime": "18:00:00"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "WEDNESDAY",
    "shiftStartTime": "09:00:00"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "WEDNESDAY",
    "shiftStartTime": "18:00:00"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "THURSDAY",
    "shiftStartTime": "09:00:00"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "THURSDAY",
    "shiftStartTime": "18:00:00"
  },
  {
    "shiftTime": "MORNING",
    "shiftEndTime": "10:30:00",
    "availableDays": "FRIDAY",
    "shiftStartTime": "09:00:00"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "FRIDAY",
    "shiftStartTime": "18:00:00"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "SATURDAY",
    "shiftStartTime": "18:00:00"
  },
  {
    "shiftTime": "EVENING",
    "shiftEndTime": "20:00:00",
    "availableDays": "SUNDAY",
    "shiftStartTime": "18:00:00"
  }
]', 500, 10, 12);

INSERT INTO `doctor_absence_information` (`clinic_id`, `doctor_id`, `doctor_name`, `absence_date`, `absence_end_time`,
                                          `absence_start_time`, `optional_message`)
VALUES (1, 'AB00001', 'Dr. Deepak Sekarbabu', CURDATE(), '20:00:00', '19:00:00', 'Personal Emergency');