package com.deepak.management.queue.service;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.model.common.ShiftTime;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.DoctorShiftAbsence;
import com.deepak.management.queue.model.DoctorShiftAvailability;
import com.deepak.management.queue.model.QueueTimeSlot;
import com.deepak.management.repository.DoctorAbsenceInformationRepository;
import com.deepak.management.repository.DoctorInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public List<QueueTimeSlot> getTimeSlotInformation(String doctorId, Integer clinicId) {
        DoctorAvailabilityInformation doctorAvailabilityInformation = this.getDetailsForSlotCreation(doctorId, clinicId);
        List<DoctorAvailability> shiftDetails = doctorAvailabilityInformation.getDoctorShiftAvailability().getShiftDetails();
        List<DoctorShiftAbsence> shiftAbsense = doctorAvailabilityInformation.getDoctorShiftAvailability().getShiftAbsense();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        int slotNo = 1;
        List<QueueTimeSlot> queueTimeSlots = new ArrayList<>();
        for (DoctorAvailability shiftDetail : shiftDetails) {
            LocalTime shiftStartTime = shiftDetail.getShiftStartTime().toLocalTime();
            LocalTime shiftEndTime = shiftDetail.getShiftEndTime().toLocalTime();
            LocalTime shiftAbsenseStartTime;
            LocalTime shiftAbsenseEndTime;
            boolean noQueueForDay   = false;
            if (shiftDetail.getShiftTime() == ShiftTime.MORNING) {
                if(!shiftAbsense.isEmpty()) {
                    for (DoctorShiftAbsence shiftAbsence : shiftAbsense) {
                        if(shiftAbsence.getShiftTime()  == ShiftTime.MORNING) {
                            shiftAbsenseStartTime = LocalTime.parse(shiftAbsence.getAbsenceStartTime(), formatter);
                            shiftAbsenseEndTime = LocalTime.parse(shiftAbsence.getAbsenceEndTime(), formatter);
                        }
                        else if(shiftAbsence.getShiftTime()  == ShiftTime.FULL_DAY) {
                            noQueueForDay = true;
                        }
                    }
                }
                if(noQueueForDay == false) {
                    while (shiftStartTime.isBefore(shiftEndTime)) {
                        QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                        queueTimeSlot.setClinicId(String.valueOf(clinicId));
                        queueTimeSlot.setDoctorId(doctorId);
                        queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                        queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                        queueTimeSlot.setSlotNo(slotNo);
                        queueTimeSlot.setSlotTime(String.valueOf(shiftStartTime));
                        queueTimeSlot.setAvailable(true);
                        slotNo++;
                        shiftStartTime = shiftStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                        queueTimeSlots.add(queueTimeSlot);
                    }
                }
            }
            if (shiftDetail.getShiftTime() == ShiftTime.AFTERNOON) {
                slotNo =1;
                while (shiftStartTime.isBefore(shiftEndTime)) {
                    QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                    queueTimeSlot.setClinicId(String.valueOf(clinicId));
                    queueTimeSlot.setDoctorId(doctorId);
                    queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                    queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                    queueTimeSlot.setSlotNo(slotNo);
                    queueTimeSlot.setSlotTime(String.valueOf(shiftStartTime));
                    queueTimeSlot.setAvailable(true);
                    slotNo++;
                    shiftStartTime = shiftStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                    queueTimeSlots.add(queueTimeSlot);
                }
            }
            if (shiftDetail.getShiftTime() == ShiftTime.EVENING) {
                slotNo =1;
                while (shiftStartTime.isBefore(shiftEndTime)) {
                    QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                    queueTimeSlot.setClinicId(String.valueOf(clinicId));
                    queueTimeSlot.setDoctorId(doctorId);
                    queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                    queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                    queueTimeSlot.setSlotNo(slotNo);
                    queueTimeSlot.setSlotTime(String.valueOf(shiftStartTime));
                    queueTimeSlot.setAvailable(true);
                    slotNo++;
                    shiftStartTime = shiftStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                    queueTimeSlots.add(queueTimeSlot);
                }
            }
            if (shiftDetail.getShiftTime() == ShiftTime.NIGHT) {
                slotNo =1;
                while (shiftStartTime.isBefore(shiftEndTime)) {
                    QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                    queueTimeSlot.setClinicId(String.valueOf(clinicId));
                    queueTimeSlot.setDoctorId(doctorId);
                    queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                    queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                    queueTimeSlot.setSlotNo(slotNo);
                    queueTimeSlot.setSlotTime(String.valueOf(shiftStartTime));
                    queueTimeSlot.setAvailable(true);
                    slotNo++;
                    shiftStartTime = shiftStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                    queueTimeSlots.add(queueTimeSlot);
                }
            }
        }

        return queueTimeSlots;
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

    private ShiftTime calculateShiftTime(Time absenceStartTime, Time absenceEndTime) {
        LocalTime localAbsenceStartTime = absenceStartTime.toLocalTime();
        LocalTime localAbsenceEndTime = absenceEndTime.toLocalTime();
        if (localAbsenceEndTime.compareTo(LocalTime.NOON) <= 0) {
            return ShiftTime.MORNING;
        } else if (localAbsenceEndTime.compareTo(LocalTime.of(16, 0)) <= 0) {
            return ShiftTime.AFTERNOON;
        } else if (localAbsenceEndTime.compareTo(LocalTime.of(22, 0)) <= 0) {
            return ShiftTime.EVENING;
        } else if (localAbsenceStartTime.compareTo(LocalTime.of(0, 0)) == 0) {
            return ShiftTime.FULL_DAY;
        } else {
            return ShiftTime.FULL_DAY;
        }
    }

    private List<DoctorAvailability> filterShiftDetails(List<DoctorAvailability> shiftDetails, String currentDayOfWeek) {
        List<DoctorAvailability> filteredList = new ArrayList<>();
        if (shiftDetails == null) {
            return filteredList;
        } else {
            for (DoctorAvailability availability : shiftDetails) {
                if (availability.getAvailableDays().name().equalsIgnoreCase(currentDayOfWeek)) {
                    filteredList.add(availability);
                }
            }
            return filteredList;
        }
    }

}