package com.deepak.management.queue.service;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.repository.DoctorInformationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueueSlotCreationServiceImpl implements QueueSlotCreationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSlotCreationServiceImpl.class);
    private final DoctorInformationRepository doctorInformationRepository;

    public QueueSlotCreationServiceImpl(DoctorInformationRepository doctorInformationRepository) {
        this.doctorInformationRepository = doctorInformationRepository;
    }

    @Override
    public List<DoctorAvailabilityInformation> getDetailsForSlotCreation(String doctorId, String clinicId) {
        List<Object[]> objects = this.doctorInformationRepository.getDoctorsWithCurrentDateAndDayOfWeek(doctorId, clinicId);
        List<DoctorAvailabilityInformation> doctorAvailabilityList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (Object[] obj : objects) {
                List<DoctorAvailability> doctorShifts = objectMapper.readValue((String) obj[0], new TypeReference<>() {
                });
                String doctorName = (String) obj[1];
                String doctorIdFromRepo = (String) obj[2];
                Integer clinicIdFromRepo = (Integer) obj[3];
                Integer doctorConsultationTime = (Integer) obj[4];
                LocalDate absenceDate = LocalDate.parse(obj[5].toString());
                LocalTime absenceEndTime = LocalTime.parse(obj[6].toString());
                LocalTime absenceStartTime = LocalTime.parse(obj[7].toString());
                java.sql.Date today = (java.sql.Date) obj[8];
                String currentDayOfWeek = (String) obj[9];

                DoctorAvailabilityInformation doctorAvailabilityInformation = new DoctorAvailabilityInformation();
                doctorAvailabilityInformation.setDoctorShifts(doctorShifts);
                doctorAvailabilityInformation.setDoctorName(doctorName);
                doctorAvailabilityInformation.setDoctorId(doctorIdFromRepo);
                doctorAvailabilityInformation.setClinicId(Math.toIntExact(clinicIdFromRepo));
                doctorAvailabilityInformation.setDoctorConsultationTime(doctorConsultationTime);
                doctorAvailabilityInformation.setAbsenceDate(absenceDate);
                doctorAvailabilityInformation.setAbsenceEndTime(absenceEndTime);
                doctorAvailabilityInformation.setAbsenceStartTime(absenceStartTime);
                doctorAvailabilityInformation.setToday(today.toLocalDate());
                doctorAvailabilityInformation.setCurrentDayOfWeek(currentDayOfWeek);

                doctorAvailabilityList.add(doctorAvailabilityInformation);
            }
            //LOGGER.info(doctorAvailabilityList.toString());
        } catch (Exception e) {
            LOGGER.error("Error occurred while parsing doctor availability info", e);
        }
        return doctorAvailabilityList;
    }
}

