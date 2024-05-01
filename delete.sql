-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Delete all rows from appointments table
DELETE FROM appointments;

-- Delete all rows from queue_management table
DELETE FROM queue_management;

-- Delete all rows from slot_information table
DELETE FROM slot_information;

-- Delete all rows from slot_generation_information table
DELETE FROM slot_generation_information;

-- Delete all rows from doctor_absence_information table
DELETE FROM doctor_absence_information;

-- Delete all rows from doctor_information table
DELETE FROM doctor_information;

-- Delete all rows from clinic_information table
DELETE FROM clinic_information;

-- Enable foreign key checks back
SET FOREIGN_KEY_CHECKS = 1;
