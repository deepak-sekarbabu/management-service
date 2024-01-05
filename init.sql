DROP TABLE IF EXISTS clinic_information;
DROP TABLE IF EXISTS doctor_absence_information;
DROP TABLE IF EXISTS doctor_information;

-- Table for clinic information
CREATE TABLE clinic_information
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

-- Table for doctor absence information
CREATE TABLE doctor_absence_information
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

-- Table for doctor information
CREATE TABLE doctor_information
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