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
import com.deepak.management.repository.SlotInformationRepository;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QueueSlotCreationServiceImpl implements QueueSlotCreationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(QueueSlotCreationServiceImpl.class);
  private final DoctorInformationRepository doctorInformationRepository;
  private final DoctorAbsenceInformationRepository doctorAbsenceInformationRepository;

  private final SlotInformationRepository slotInformationRepository;

  public QueueSlotCreationServiceImpl(
      DoctorInformationRepository doctorInformationRepository,
      DoctorAbsenceInformationRepository doctorAbsenceInformationRepository,
      SlotInformationRepository slotInformationRepository) {
    this.doctorInformationRepository = doctorInformationRepository;
    this.doctorAbsenceInformationRepository = doctorAbsenceInformationRepository;
    this.slotInformationRepository = slotInformationRepository;
  }

  private static void removeDoctorAbsence(
      List<QueueTimeSlot> queueTimeSlots, List<QueueTimeSlot> queueAbsenceTimeSlots) {
    queueTimeSlots.removeIf(
        slot1 ->
            queueAbsenceTimeSlots.stream()
                .anyMatch(slot2 -> slot1.getSlotTime().equals(slot2.getSlotTime())));
  }

  private static void reorderQueueNumbers(List<QueueTimeSlot> queueTimeSlots) {
    int slotNoMorning = 1;
    int slotNoAfternoon = 1;
    int slotNoEvening = 1;
    for (QueueTimeSlot slot : queueTimeSlots) {
      if (slot.getShiftTime().equalsIgnoreCase("MORNING")) {
        slot.setSlotNo(slotNoMorning);
        slotNoMorning++;
      } else if (slot.getShiftTime().equalsIgnoreCase("AFTERNOON")) {
        slot.setSlotNo(slotNoAfternoon);
        slotNoAfternoon++;
      } else if (slot.getShiftTime().equalsIgnoreCase("EVENING")) {
        slot.setSlotNo(slotNoEvening);
        slotNoEvening++;
      }
    }
  }

  @Override
  public DoctorAvailabilityInformation getDetailsForSlotCreation(
      String doctorId, Integer clinicId) {
    final DoctorInformation information =
        this.doctorInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    final List<DoctorAbsenceInformation> absenceInformation =
        this.doctorAbsenceInformationRepository.findByAbsenceDateAndClinicIdAndDoctorId(
            Date.valueOf(LocalDate.now()), clinicId, doctorId);
    LOGGER.info("Doctor Availability Information: {}", information);
    LOGGER.info("Doctor Absence Information: {}", absenceInformation);

    final DoctorAvailabilityInformation doctorAvailabilityInformation =
        new DoctorAvailabilityInformation();
    final DoctorShiftAvailability doctorShiftAvailability = new DoctorShiftAvailability();

    doctorShiftAvailability.setDoctorName(information.getDoctorName());
    doctorShiftAvailability.setDoctorId(information.getDoctorId());
    doctorShiftAvailability.setClinicId(information.getClinicId());
    doctorShiftAvailability.setShiftDetails(
        this.filterShiftDetails(
            information.getDoctorAvailability(), LocalDate.now().getDayOfWeek().toString()));
    doctorShiftAvailability.setShiftAbsence(this.filterAbsenceInformation(absenceInformation));
    // doctorShiftAvailability.setDoctorConsultationTime(information.getDoctorConsultationTime());
    doctorAvailabilityInformation.setDoctorShiftAvailability(doctorShiftAvailability);
    doctorAvailabilityInformation.setCurrentDate(String.valueOf(LocalDate.now()));
    doctorAvailabilityInformation.setCurrentDayOfWeek(LocalDate.now().getDayOfWeek().toString());
    return doctorAvailabilityInformation;
  }

  @Override
  public List<QueueTimeSlot> getTimeSlotInformation(String doctorId, Integer clinicId) {
    final DoctorAvailabilityInformation doctorAvailabilityInformation =
        this.getDetailsForSlotCreation(doctorId, clinicId);
    final List<DoctorAvailability> shiftDetails =
        doctorAvailabilityInformation.getDoctorShiftAvailability().getShiftDetails();
    final List<DoctorShiftAbsence> shiftAbsences =
        doctorAvailabilityInformation.getDoctorShiftAvailability().getShiftAbsence();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    int slotNo;
    final List<QueueTimeSlot> queueTimeSlots = new ArrayList<>();
    final List<QueueTimeSlot> queueAbsenceTimeSlots = new ArrayList<>();

    for (DoctorAvailability shiftDetail : shiftDetails) {
      final ShiftTime shiftTime = shiftDetail.getShiftTime();
      final List<DoctorShiftAbsence> shiftAbsencesForShiftTime =
          shiftAbsences.stream()
              .filter(
                  shiftAbsence ->
                      shiftAbsence.getShiftTime() == shiftTime
                          || shiftAbsence.getShiftTime() == ShiftTime.FULL_DAY)
              .toList();

      final boolean noQueueForDay =
          shiftAbsencesForShiftTime.stream()
              .anyMatch(shiftAbsence -> shiftAbsence.getShiftTime() == ShiftTime.FULL_DAY);

      LocalTime shiftStartTime = shiftDetail.getShiftStartTime().toLocalTime();
      LocalTime shiftEndTime = shiftDetail.getShiftEndTime().toLocalTime();
      LocalTime shiftAbsenceStartTime = null;
      LocalTime shiftAbsenceEndTime = null;

      for (DoctorShiftAbsence shiftAbsence : shiftAbsencesForShiftTime) {
        if (shiftAbsence.getShiftTime() == shiftTime) {
          shiftAbsenceStartTime = LocalTime.parse(shiftAbsence.getAbsenceStartTime(), formatter);
          shiftAbsenceEndTime = LocalTime.parse(shiftAbsence.getAbsenceEndTime(), formatter);
          break;
        }
      }

      slotNo = 1;
      if (!noQueueForDay) {
        while (shiftStartTime.isBefore(shiftEndTime)) {
          final QueueTimeSlot queueTimeSlot =
              createQueueTimeSlot(clinicId, doctorId, shiftDetail, slotNo, shiftStartTime, true);
          queueTimeSlots.add(queueTimeSlot);
          shiftStartTime = shiftStartTime.plusMinutes(shiftDetail.getConsultationTime());
          slotNo++;
        }

        while (shiftAbsenceStartTime != null
            && shiftAbsenceStartTime.isBefore(shiftAbsenceEndTime)) {
          QueueTimeSlot queueTimeSlot =
              createQueueTimeSlot(
                  clinicId, doctorId, shiftDetail, slotNo, shiftAbsenceStartTime, false);
          queueAbsenceTimeSlots.add(queueTimeSlot);
          shiftAbsenceStartTime =
              shiftAbsenceStartTime.plusMinutes(shiftDetail.getConsultationTime());
          slotNo++;
        }
      }
    }

    LOGGER.info("Queue Time Slot List : {}", queueTimeSlots);
    LOGGER.info("Queue Absence Time Slot List : {}", queueAbsenceTimeSlots);

    removeDoctorAbsence(queueTimeSlots, queueAbsenceTimeSlots);

    LOGGER.info("Queue Time Slot List After removing absence information: {}", queueTimeSlots);
    reorderQueueNumbers(queueTimeSlots);

    slotInformationRepository.saveAll(queueTimeSlots);

    LOGGER.info("Queue Time Slot List After sorting : {}", queueTimeSlots);
    return queueTimeSlots;
  }

  private QueueTimeSlot createQueueTimeSlot(
      Integer clinicId,
      String doctorId,
      DoctorAvailability shiftDetail,
      int slotNo,
      LocalTime slotTime,
      boolean isAvailable) {
    final QueueTimeSlot queueTimeSlot = new QueueTimeSlot();
    queueTimeSlot.setClinicId(clinicId);
    queueTimeSlot.setDoctorId(doctorId);
    queueTimeSlot.setSlotDate(String.valueOf(LocalDate.now()));
    queueTimeSlot.setShiftTime(String.valueOf(shiftDetail.getShiftTime()));
    queueTimeSlot.setSlotNo(slotNo);
    queueTimeSlot.setSlotTime(String.valueOf(slotTime));
    queueTimeSlot.setAvailable(isAvailable);
    return queueTimeSlot;
  }

  private List<DoctorShiftAbsence> filterAbsenceInformation(
      List<DoctorAbsenceInformation> absenceInformation) {
    final List<DoctorShiftAbsence> doctorShiftAbsenceList = new ArrayList<>();
    if (absenceInformation == null) {
      return doctorShiftAbsenceList;
    } else {
      for (DoctorAbsenceInformation information : absenceInformation) {
        DoctorShiftAbsence doctorShiftAbsence = createDoctorShiftAbsence(information);
        doctorShiftAbsenceList.add(doctorShiftAbsence);
      }
    }
    return doctorShiftAbsenceList;
  }

  private DoctorShiftAbsence createDoctorShiftAbsence(DoctorAbsenceInformation information) {
    DoctorShiftAbsence doctorShiftAbsence = new DoctorShiftAbsence();
    doctorShiftAbsence.setAbsenceDay(LocalDate.now().getDayOfWeek().toString());
    doctorShiftAbsence.setShiftTime(
        calculateShiftTime(information.getAbsenceStartTime(), information.getAbsenceEndTime()));
    doctorShiftAbsence.setAbsenceStartTime(String.valueOf(information.getAbsenceStartTime()));
    doctorShiftAbsence.setAbsenceEndTime(String.valueOf(information.getAbsenceEndTime()));
    return doctorShiftAbsence;
  }

  private ShiftTime calculateShiftTime(Time absenceStartTime, Time absenceEndTime) {
    final LocalTime localAbsenceStartTime = absenceStartTime.toLocalTime();
    final LocalTime localAbsenceEndTime = absenceEndTime.toLocalTime();
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

  private List<DoctorAvailability> filterShiftDetails(
      List<DoctorAvailability> shiftDetails, String currentDayOfWeek) {
    final List<DoctorAvailability> filteredList = new ArrayList<>();
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
