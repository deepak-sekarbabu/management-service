CREATE TABLE clinic_information
(
    clinic_id            INTEGER NOT NULL AUTO_INCREMENT,
    no_of_doctors        INTEGER CHECK ((no_of_doctors <= 100) AND (no_of_doctors >= 1)),
    clinic_pin_code      VARCHAR(10),
    map_geo_location     VARCHAR(50),
    clinic_email         VARCHAR(120),
    clinic_name          VARCHAR(150),
    clinic_address       VARCHAR(200),
    clinic_phone_numbers JSON,
    PRIMARY KEY (clinic_id)
) ENGINE=InnoDB;

CREATE TABLE doctor_information
(
    clinic_id           INTEGER,
    id                  INTEGER NOT NULL AUTO_INCREMENT,
    doctor_name         VARCHAR(120),
    doctor_speciality   VARCHAR(120),
    doctor_id           VARCHAR(255),
    doctor_availability JSON,
    phone_numbers       JSON,
    PRIMARY KEY (id),
    FOREIGN KEY (clinic_id) REFERENCES clinic_information (clinic_id)
) ENGINE=InnoDB;