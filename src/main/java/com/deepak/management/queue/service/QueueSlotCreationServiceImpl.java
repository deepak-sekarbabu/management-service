package com.deepak.management.queue.service;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.DoctorShiftAbsence;
import com.deepak.management.queue.model.DoctorShiftAvailability;
import com.deepak.management.repository.DoctorInformationRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                DoctorAvailabilityInformation doctorAvailabilityInformation = new DoctorAvailabilityInformation();
                DoctorShiftAvailability doctorShiftAvailability = new DoctorShiftAvailability();
                DoctorShiftAbsence doctorShiftAbsence = new DoctorShiftAbsence();
                List<DoctorAvailability> shiftDetails = null;
                if (obj[0] != null) {
                    shiftDetails = objectMapper.readValue((String) obj[0], new TypeReference<>() {
                    });
                }

                String doctorName = (String) obj[1];
                String doctorIdFromRepo = (String) obj[2];
                Integer clinicIdFromRepo = (Integer) obj[3];
                Integer doctorConsultationTime = (Integer) obj[4];

                if (obj[5] != null) {
                    LocalDate absenceDate = LocalDate.parse(obj[5].toString());
                    doctorShiftAbsence.setAbsenceDate(absenceDate);
                }
                if (obj[6] != null) {
                    LocalTime absenceEndTime = LocalTime.parse(obj[6].toString());
                    doctorShiftAbsence.setAbsenceEndTime(absenceEndTime);
                }
                if (obj[7] != null) {
                    LocalTime absenceStartTime = LocalTime.parse(obj[7].toString());
                    doctorShiftAbsence.setAbsenceStartTime(absenceStartTime);
                }

                Date today = (Date) obj[8];
                String currentDayOfWeek = (String) obj[9];
                doctorAvailabilityInformation.setCurrentDate(today.toLocalDate());
                doctorAvailabilityInformation.setCurrentDayOfWeek(currentDayOfWeek);

                doctorShiftAvailability.setShiftDetails(filterShiftDetails(shiftDetails, currentDayOfWeek));
                doctorShiftAvailability.setDoctorName(doctorName);
                doctorShiftAvailability.setDoctorId(doctorIdFromRepo);
                doctorShiftAvailability.setClinicId(clinicIdFromRepo);
                doctorShiftAvailability.setDoctorConsultationTime(doctorConsultationTime);
                doctorAvailabilityInformation.setDoctorShiftAvailability(doctorShiftAvailability);

                doctorShiftAbsence.setDoctorName(doctorName);
                doctorShiftAbsence.setDoctorId(doctorIdFromRepo);
                doctorShiftAbsence.setClinicId(clinicIdFromRepo);

                if (obj[5] != null) {
                    doctorAvailabilityInformation.setDoctorShiftAbsence(doctorShiftAbsence);
                }

                doctorAvailabilityList.add(doctorAvailabilityInformation);
            }
            //LOGGER.info(doctorAvailabilityList.toString());
        } catch (Exception e) {
            LOGGER.error("Error occurred while parsing doctor availability info", e);
        }
        return doctorAvailabilityList;
    }

    @Override
    public List<DoctorAvailability> getDoctorShiftsForDay(String day, List<DoctorAvailability> availabilities) {
        return null;
    }

    private List<DoctorAvailability> filterShiftDetails(List<DoctorAvailability> shiftDetails, String currentDayOfWeek) {
        List<DoctorAvailability> filteredList = new ArrayList<>();
        if (shiftDetails == null) {
            return filteredList;
        } else {
            for (DoctorAvailability availability : shiftDetails) {
                // Check if the DoctorAvailability instance matches the specified day
                if (availability.getAvailableDays().name().equalsIgnoreCase(currentDayOfWeek)) {
                    filteredList.add(availability); // Add matching availability to the filtered list
                }
            }
            return filteredList;
        }
    }

}