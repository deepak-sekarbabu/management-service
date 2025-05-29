-- SQL Inserts --
INSERT INTO clinic_information (
 clinic_name,
 clinic_address,
 clinic_pin_code,
 map_geo_location,
 clinic_amenities,
 clinic_email,
 clinic_timing,
 clinic_website,
 clinic_phone_numbers,
 no_of_doctors
) VALUES (
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

INSERT INTO doctor_information (
 doctor_id,
 clinic_id,
 doctor_name,
 phone_numbers,
 doctor_speciality,
 doctor_availability,
 doctor_consultation_fee,
 doctor_consultation_fee_other,
 doctor_experience,
 languages_spoken,
 qualifications
) VALUES (
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
 12,
 '["Tamil", "English", "Malayalam"]',
 '["MBBS", "MD (Paediatrics)", "DCH"]'
);

INSERT INTO doctor_absence_information (
 clinic_id,
 doctor_id,
 doctor_name,
 absence_date,
 absence_start_time,
 absence_end_time,
 optional_message
) VALUES (
 1,
 'AB00001',
 'Dr. Deepak Sekarbabu', CURDATE(),
 '10:00:00',
 '11:00:00',
 'Personal Emergency'
);

INSERT INTO doctor_absence_information (
 clinic_id,
 doctor_id,
 doctor_name,
 absence_date,
 absence_end_time,
 absence_start_time,
 optional_message
) VALUES (
 1,
 'AB00001',
 'Dr. Deepak Sekarbabu', CURDATE(),
 '19:00:00',
 '18:00:00',
 'Personal Emergency'
);

INSERT INTO users (name, phoneNumber, email, birthdate) VALUES
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

INSERT INTO appointments (
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
) VALUES (
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

