-- Disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Truncate tables
TRUNCATE TABLE QueueManagement.appointments;
TRUNCATE TABLE QueueManagement.queue_management;
TRUNCATE TABLE QueueManagement.slot_information;
TRUNCATE TABLE QueueManagement.slot_generation_information;
TRUNCATE TABLE QueueManagement.doctor_absence_information;
TRUNCATE TABLE QueueManagement.doctor_information;
TRUNCATE TABLE QueueManagement.clinic_information;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
