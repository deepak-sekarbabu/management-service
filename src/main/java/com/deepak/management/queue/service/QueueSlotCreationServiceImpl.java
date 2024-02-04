package com.deepak.management.queue.service;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.DoctorShiftAbsence;
import com.deepak.management.queue.model.DoctorShiftAvailability;
import com.deepak.management.repository.DoctorAbsenceInformationRepository;
import com.deepak.management.repository.DoctorInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueueSlotCreationServiceImpl implements QueueSlotCreationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueSlotCreationServiceImpl.class);
    private final DoctorInformationRepository doctorInformationRepository;
    private final DoctorAbsenceInformationRepository doctorAbsenceInformationRepository;

    public QueueSlotCreationServiceImpl(DoctorInformationRepository doctorInformationRepository, DoctorAbsenceInformationRepository doctorAbsenceInformationRepository) {
        this.doctorInformationRepository = doctorInformationRepository;
        this.doctorAbsenceInformationRepository = doctorAbsenceInformationRepository;
    }

    @Override
    public DoctorAvailabilityInformation getDetailsForSlotCreation(String doctorId, Integer clinicId) {
        DoctorInformation information = doctorInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
        List<DoctorAbsenceInformation> absenceInformation = doctorAbsenceInformationRepository.findByAbsenceDateAndClinicIdAndDoctorId(Date.valueOf(LocalDate.now()), clinicId, doctorId);
        LOGGER.info("Doctor Availability Information: {}", information);
        LOGGER.info("Doctor Absence Information: {}", absenceInformation);

        DoctorAvailabilityInformation doctorAvailabilityInformation = new DoctorAvailabilityInformation();

        DoctorShiftAvailability doctorShiftAvailability = new DoctorShiftAvailability();
        doctorShiftAvailability.setDoctorName(information.getDoctorName());
        doctorShiftAvailability.setDoctorId(information.getDoctorId());
        doctorShiftAvailability.setClinicId(information.getClinicId());

        doctorShiftAvailability.setShiftDetails(filterShiftDetails(information.getDoctorAvailability(), LocalDate.now().getDayOfWeek().toString()));
        doctorShiftAvailability.setShiftAbsense(filterAbsenceInformation(absenceInformation));


        doctorShiftAvailability.setDoctorConsultationTime(information.getDoctorConsultationTime());
        doctorAvailabilityInformation.setDoctorShiftAvailability(doctorShiftAvailability);


        doctorAvailabilityInformation.setCurrentDate(String.valueOf(LocalDate.now()));
        doctorAvailabilityInformation.setCurrentDayOfWeek(LocalDate.now().getDayOfWeek().toString());
        return doctorAvailabilityInformation;
    }

    private List<DoctorShiftAbsence> filterAbsenceInformation(List<DoctorAbsenceInformation> absenceInformation) {
        List<DoctorShiftAbsence> doctorShiftAbsenceList = new ArrayList<>();
        if (absenceInformation == null) {
            return doctorShiftAbsenceList;
        } else {
            for (DoctorAbsenceInformation information : absenceInformation) {
                DoctorShiftAbsence doctorShiftAbsence = new DoctorShiftAbsence();
                doctorShiftAbsence.setAbsenseDay(LocalDate.now().getDayOfWeek().toString());
                doctorShiftAbsence.setShiftTime(calculateShiftTime(information.getAbsenceStartTime(), information.getAbsenceEndTime()));
                doctorShiftAbsence.setAbsenceStartTime(String.valueOf(information.getAbsenceStartTime()));
                doctorShiftAbsence.setAbsenceEndTime(String.valueOf(information.getAbsenceEndTime()));
                doctorShiftAbsenceList.add(doctorShiftAbsence);

            }
        }
        return doctorShiftAbsenceList;
    }

    private String calculateShiftTime(Time absenceStartTime, Time absenceEndTime) {
        LocalTime localAbsenceStartTime = absenceStartTime.toLocalTime();
        LocalTime localAbsenceEndTime = absenceEndTime.toLocalTime();
        if (localAbsenceEndTime.compareTo(LocalTime.NOON) <= 0) {
            return "MORNING";
        } else if (localAbsenceEndTime.compareTo(LocalTime.of(16, 0)) <= 0) {
            return "AFTERNOON";
        } else if (localAbsenceEndTime.compareTo(LocalTime.of(22, 0)) <= 0) {
            return "EVENING";
        } else {
            return "FULL_DAY";
        }
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