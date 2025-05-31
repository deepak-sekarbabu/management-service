package com.deepak.management.queue.service;

import com.deepak.management.model.common.DoctorAvailability;
import com.deepak.management.model.common.ShiftTime;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.queue.model.DoctorAvailabilityInformation;
import com.deepak.management.queue.model.DoctorShiftAbsence;
import com.deepak.management.queue.model.DoctorShiftAvailability;
import com.deepak.management.queue.model.QueueTimeSlot;
import com.deepak.management.queue.model.SlotGeneration;
import com.deepak.management.repository.DoctorAbsenceInformationRepository;
import com.deepak.management.repository.DoctorInformationRepository;
import com.deepak.management.repository.SlotGenerationRepository;
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
  private final SlotGenerationRepository slotGenerationRepository;

  public QueueSlotCreationServiceImpl(
      DoctorInformationRepository doctorInformationRepository,
      DoctorAbsenceInformationRepository doctorAbsenceInformationRepository,
      SlotInformationRepository slotInformationRepository,
      SlotGenerationRepository slotGenerationRepository) {
    this.doctorInformationRepository = doctorInformationRepository;
    this.doctorAbsenceInformationRepository = doctorAbsenceInformationRepository;
    this.slotInformationRepository = slotInformationRepository;
    this.slotGenerationRepository = slotGenerationRepository;
  }

  private static final int BATCH_SIZE = 100;

  private static void removeDoctorAbsence(
      List<QueueTimeSlot> queueTimeSlots, List<QueueTimeSlot> queueAbsenceTimeSlots) {
    if (queueTimeSlots == null
        || queueAbsenceTimeSlots == null
        || queueTimeSlots.isEmpty()
        || queueAbsenceTimeSlots.isEmpty()) {
      return;
    }
    queueTimeSlots.removeIf(
        slot1 ->
            queueAbsenceTimeSlots.stream()
                .anyMatch(
                    slot2 ->
                        slot1 != null
                            && slot2 != null
                            && slot1.getSlotTime() != null
                            && slot1.getSlotTime().equals(slot2.getSlotTime())));
  }

  private static void reorderQueueNumbers(List<QueueTimeSlot> queueTimeSlots) {
    if (queueTimeSlots == null || queueTimeSlots.isEmpty()) {
      return;
    }

    int slotNoMorning = 1;
    int slotNoAfternoon = 1;
    int slotNoEvening = 1;

    for (QueueTimeSlot slot : queueTimeSlots) {
      if (slot == null || slot.getShiftTime() == null) {
        continue;
      }

      switch (slot.getShiftTime().toUpperCase()) {
        case "MORNING" -> slot.setSlotNo(slotNoMorning++);
        case "AFTERNOON" -> slot.setSlotNo(slotNoAfternoon++);
        case "EVENING" -> slot.setSlotNo(slotNoEvening++);
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
    final List<QueueTimeSlot> queueTimeSlots =
        this.slotInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    LOGGER.info("Doctor Availability Information: {}", information);
    LOGGER.info("Doctor Absence Information: {}", absenceInformation);
    LOGGER.info("Queue Time Slot List: {}", queueTimeSlots);

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
    doctorAvailabilityInformation.setQueueTimeSlots(queueTimeSlots);
    return doctorAvailabilityInformation;
  }

  @Override
  public List<QueueTimeSlot> getTimeSlotInformation(String doctorId, Integer clinicId) {
    // Check if slot generation already exists for today
    Date today = Date.valueOf(LocalDate.now());
    if (slotGenerationRepository
        .findByDoctorIdAndClinicIdAndSlotDate(doctorId, clinicId, today)
        .isPresent()) {
      LOGGER.info(
          "Slot generation already exists for doctor {} and clinic {} for today",
          doctorId,
          clinicId);
      return slotInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId).stream()
          .filter(slot -> slot.getSlotDate().equals(String.valueOf(LocalDate.now())))
          .toList();
    }

    // Continue with slot generation if no slots exist for today
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
      if (!noQueueForDay) { // Add safety check for max slots per shift
        int maxSlotsPerShift = 100; // Reasonable limit for slots per shift
        int currentSlotCount = 0;

        while (shiftStartTime.isBefore(shiftEndTime) && currentSlotCount < maxSlotsPerShift) {
          // Add safety check to prevent infinite loop
          if (shiftDetail.getConsultationTime() <= 0) {
            LOGGER.error(
                "Invalid consultation time of {} minutes", shiftDetail.getConsultationTime());
            break;
          }

          final QueueTimeSlot queueTimeSlot =
              createQueueTimeSlot(clinicId, doctorId, shiftDetail, slotNo, shiftStartTime, true);
          queueTimeSlots.add(queueTimeSlot);
          shiftStartTime = shiftStartTime.plusMinutes(shiftDetail.getConsultationTime());
          slotNo++;
          currentSlotCount++;
        }

        // Reset counter for absence slots
        currentSlotCount = 0;
        while (shiftAbsenceStartTime != null
            && shiftAbsenceStartTime.isBefore(shiftAbsenceEndTime)
            && currentSlotCount < maxSlotsPerShift) {
          QueueTimeSlot queueTimeSlot =
              createQueueTimeSlot(
                  clinicId, doctorId, shiftDetail, slotNo, shiftAbsenceStartTime, false);
          queueAbsenceTimeSlots.add(queueTimeSlot);
          shiftAbsenceStartTime =
              shiftAbsenceStartTime.plusMinutes(shiftDetail.getConsultationTime());
          slotNo++;
          currentSlotCount++;
        }
      }
    }
    LOGGER.info("Generated {} slots, processing in batches...", queueTimeSlots.size());

    removeDoctorAbsence(queueTimeSlots, queueAbsenceTimeSlots);
    reorderQueueNumbers(queueTimeSlots);

    // Save in batches to prevent memory issues
    for (int i = 0; i < queueTimeSlots.size(); i += BATCH_SIZE) {
      int end = Math.min(i + BATCH_SIZE, queueTimeSlots.size());
      List<QueueTimeSlot> batch = queueTimeSlots.subList(i, end);
      slotInformationRepository.saveAll(batch);
      LOGGER.info("Saved batch {} to {}", i, end);
    }

    // After successful slot generation, record it in slot_generation_information
    SlotGeneration slotGeneration = new SlotGeneration();
    slotGeneration.setDoctorId(doctorId);
    slotGeneration.setClinicId(clinicId);
    slotGeneration.setSlotDate(today);
    slotGeneration.setStatus(true);
    slotGeneration.setNoOfSlots(queueTimeSlots.size());
    slotGenerationRepository.save(slotGeneration);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Final queue slots: {}", queueTimeSlots);
    }
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
    if (!localAbsenceEndTime.isAfter(LocalTime.NOON)) {
      return ShiftTime.MORNING;
    } else if (!localAbsenceEndTime.isAfter(LocalTime.of(16, 0))) {
      return ShiftTime.AFTERNOON;
    } else if (!localAbsenceEndTime.isAfter(LocalTime.of(22, 0))) {
      return ShiftTime.EVENING;
    } else if (localAbsenceStartTime.equals(LocalTime.of(0, 0))) {
      return ShiftTime.FULL_DAY;
    } else {
      return ShiftTime.FULL_DAY;
    }
  }

  private List<DoctorAvailability> filterShiftDetails(
      List<DoctorAvailability> shiftDetails, String currentDayOfWeek) {
    final List<DoctorAvailability> filteredList = new ArrayList<>();
    if (shiftDetails != null) {
      for (DoctorAvailability availability : shiftDetails) {
        if (availability.getAvailableDays().name().equalsIgnoreCase(currentDayOfWeek)) {
          filteredList.add(availability);
        }
      }
    }
    return filteredList;
  }
}
