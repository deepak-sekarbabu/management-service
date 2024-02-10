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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        DoctorInformation information = this.doctorInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
        List<DoctorAbsenceInformation> absenceInformation = this.doctorAbsenceInformationRepository.findByAbsenceDateAndClinicIdAndDoctorId(Date.valueOf(LocalDate.now()), clinicId, doctorId);
        LOGGER.info("Doctor Availability Information: {}", information);
        LOGGER.info("Doctor Absence Information: {}", absenceInformation);

        DoctorAvailabilityInformation doctorAvailabilityInformation = new DoctorAvailabilityInformation();

        DoctorShiftAvailability doctorShiftAvailability = new DoctorShiftAvailability();
        doctorShiftAvailability.setDoctorName(information.getDoctorName());
        doctorShiftAvailability.setDoctorId(information.getDoctorId());
        doctorShiftAvailability.setClinicId(information.getClinicId());

        doctorShiftAvailability.setShiftDetails(this.filterShiftDetails(information.getDoctorAvailability(), LocalDate.now().getDayOfWeek().toString()));
        doctorShiftAvailability.setShiftAbsense(this.filterAbsenceInformation(absenceInformation));


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
        List<QueueTimeSlot> queueAbsenceTimeSlots = new ArrayList<>();
        for (DoctorAvailability shiftDetail : shiftDetails) {
            LocalTime shiftStartTime = shiftDetail.getShiftStartTime().toLocalTime();
            LocalTime shiftEndTime = shiftDetail.getShiftEndTime().toLocalTime();
            LocalTime shiftAbsenseStartTime = null;
            LocalTime shiftAbsenseEndTime = null;
            boolean noQueueForDay = false;
            if (shiftDetail.getShiftTime() == ShiftTime.MORNING) {
                if (!shiftAbsense.isEmpty()) {
                    for (DoctorShiftAbsence shiftAbsence : shiftAbsense) {
                        if (shiftAbsence.getShiftTime() == ShiftTime.MORNING) {
                            shiftAbsenseStartTime = LocalTime.parse(shiftAbsence.getAbsenceStartTime(), formatter);
                            shiftAbsenseEndTime = LocalTime.parse(shiftAbsence.getAbsenceEndTime(), formatter);
                        } else if (shiftAbsence.getShiftTime() == ShiftTime.FULL_DAY) {
                            noQueueForDay = true;
                        }
                    }
                }
                slotNo = 1;
                if (!noQueueForDay) {
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
                    while (shiftAbsenseStartTime.isBefore(shiftAbsenseEndTime)) {
                        QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                        queueTimeSlot.setClinicId(String.valueOf(clinicId));
                        queueTimeSlot.setDoctorId(doctorId);
                        queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                        queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                        queueTimeSlot.setSlotTime(String.valueOf(shiftAbsenseStartTime));
                        queueTimeSlot.setAvailable(false);
                        shiftAbsenseStartTime = shiftAbsenseStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                        queueAbsenceTimeSlots.add(queueTimeSlot);
                    }
                }
            }
            if (shiftDetail.getShiftTime() == ShiftTime.AFTERNOON) {
                if (!shiftAbsense.isEmpty()) {
                    for (DoctorShiftAbsence shiftAbsence : shiftAbsense) {
                        if (shiftAbsence.getShiftTime() == ShiftTime.AFTERNOON) {
                            shiftAbsenseStartTime = LocalTime.parse(shiftAbsence.getAbsenceStartTime(), formatter);
                            shiftAbsenseEndTime = LocalTime.parse(shiftAbsence.getAbsenceEndTime(), formatter);
                        } else if (shiftAbsence.getShiftTime() == ShiftTime.FULL_DAY) {
                            noQueueForDay = true;
                        }
                    }
                }
                slotNo = 1;
                if (!noQueueForDay) {
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
                    while (shiftAbsenseStartTime.isBefore(shiftAbsenseEndTime)) {
                        QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                        queueTimeSlot.setClinicId(String.valueOf(clinicId));
                        queueTimeSlot.setDoctorId(doctorId);
                        queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                        queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                        queueTimeSlot.setSlotTime(String.valueOf(shiftAbsenseStartTime));
                        queueTimeSlot.setAvailable(false);
                        shiftAbsenseStartTime = shiftAbsenseStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                        queueAbsenceTimeSlots.add(queueTimeSlot);
                    }
                }
            }
            if (shiftDetail.getShiftTime() == ShiftTime.EVENING) {
                if (!shiftAbsense.isEmpty()) {
                    for (DoctorShiftAbsence shiftAbsence : shiftAbsense) {
                        if (shiftAbsence.getShiftTime() == ShiftTime.EVENING) {
                            shiftAbsenseStartTime = LocalTime.parse(shiftAbsence.getAbsenceStartTime(), formatter);
                            shiftAbsenseEndTime = LocalTime.parse(shiftAbsence.getAbsenceEndTime(), formatter);
                        } else if (shiftAbsence.getShiftTime() == ShiftTime.FULL_DAY) {
                            noQueueForDay = true;
                        }
                    }
                }
                slotNo = 1;
                if (!noQueueForDay) {
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
                    while (shiftAbsenseStartTime.isBefore(shiftAbsenseEndTime)) {
                        QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                        queueTimeSlot.setClinicId(String.valueOf(clinicId));
                        queueTimeSlot.setDoctorId(doctorId);
                        queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                        queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                        queueTimeSlot.setSlotTime(String.valueOf(shiftAbsenseStartTime));
                        queueTimeSlot.setAvailable(false);
                        shiftAbsenseStartTime = shiftAbsenseStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                        queueAbsenceTimeSlots.add(queueTimeSlot);
                    }
                }
            }
            if (shiftDetail.getShiftTime() == ShiftTime.NIGHT) {
                if (!shiftAbsense.isEmpty()) {
                    for (DoctorShiftAbsence shiftAbsence : shiftAbsense) {
                        if (shiftAbsence.getShiftTime() == ShiftTime.NIGHT) {
                            shiftAbsenseStartTime = LocalTime.parse(shiftAbsence.getAbsenceStartTime(), formatter);
                            shiftAbsenseEndTime = LocalTime.parse(shiftAbsence.getAbsenceEndTime(), formatter);
                        } else if (shiftAbsence.getShiftTime() == ShiftTime.FULL_DAY) {
                            noQueueForDay = true;
                        }
                    }
                }
                slotNo = 1;
                if (!noQueueForDay) {
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
                    while (shiftAbsenseStartTime.isBefore(shiftAbsenseEndTime)) {
                        QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
                        queueTimeSlot.setClinicId(String.valueOf(clinicId));
                        queueTimeSlot.setDoctorId(doctorId);
                        queueTimeSlot.setDate(String.valueOf(LocalDate.now()));
                        queueTimeSlot.setShift(String.valueOf(shiftDetail.getShiftTime()));
                        queueTimeSlot.setSlotTime(String.valueOf(shiftAbsenseStartTime));
                        queueTimeSlot.setAvailable(false);
                        shiftAbsenseStartTime = shiftAbsenseStartTime.plusMinutes(doctorAvailabilityInformation.getDoctorShiftAvailability().getDoctorConsultationTime());
                        queueAbsenceTimeSlots.add(queueTimeSlot);
                    }
                }
            }
        }
        LOGGER.info("Queue Time Slot List : {}", queueTimeSlots);
        LOGGER.info("Queue Absence Time Slot List : {}", queueAbsenceTimeSlots);

        removeDoctorAbsense(queueTimeSlots, queueAbsenceTimeSlots);

        LOGGER.info("Queue Time Slot List After removing absence information: {}", queueTimeSlots);
        return queueTimeSlots;
    }


    private static void removeDoctorAbsense(List<QueueTimeSlot> queueTimeSlots, List<QueueTimeSlot> queueAbsenceTimeSlots) {
        queueTimeSlots.removeIf(slot1 ->
                queueAbsenceTimeSlots.stream()
                        .anyMatch(slot2 -> slot1.getSlotTime().equals(slot2.getSlotTime()))
        );
    }


    private List<DoctorShiftAbsence> filterAbsenceInformation(List<DoctorAbsenceInformation> absenceInformation) {
        List<DoctorShiftAbsence> doctorShiftAbsenceList = new ArrayList<>();
        if (absenceInformation == null) {
            return doctorShiftAbsenceList;
        } else {
            for (DoctorAbsenceInformation information : absenceInformation) {
                DoctorShiftAbsence doctorShiftAbsence = new DoctorShiftAbsence();
                doctorShiftAbsence.setAbsenseDay(LocalDate.now().getDayOfWeek().toString());
                doctorShiftAbsence.setShiftTime(this.calculateShiftTime(information.getAbsenceStartTime(), information.getAbsenceEndTime()));
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