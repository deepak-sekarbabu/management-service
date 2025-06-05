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
import java.time.LocalDateTime;
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

  /**
   * Removes time slots from a list of queue time slots if they coincide with any doctor's absence
   * periods.
   *
   * <p>This method iterates through the {@code queueTimeSlots} and removes any slot whose time
   * matches a slot time present in the {@code queueAbsenceTimeSlots} list. The comparison is based
   * on the exact time of the slot.
   *
   * @param queueTimeSlots A list of {@link QueueTimeSlot} objects, representing all initially
   *     generated slots. This list is modified in place.
   * @param queueAbsenceTimeSlots A list of {@link QueueTimeSlot} objects, representing the periods
   *     when the doctor is absent.
   */
  private static void removeDoctorAbsence(
      List<QueueTimeSlot> queueTimeSlots, List<QueueTimeSlot> queueAbsenceTimeSlots) {
    if (queueTimeSlots == null
        || queueAbsenceTimeSlots == null
        || queueTimeSlots.isEmpty()
        || queueAbsenceTimeSlots.isEmpty()) {
      return; // No action needed if either list is null or empty
    }
    // Remove slots from queueTimeSlots if their slotTime matches any slotTime in
    // queueAbsenceTimeSlots
    queueTimeSlots.removeIf(
        slot1 -> // Iterate through each slot in the main list
        queueAbsenceTimeSlots.stream() // Stream the absence slots for comparison
                .anyMatch( // Check if any absence slot matches the current main slot
                    slot2 ->
                        slot1 != null // Ensure main slot is not null
                            && slot2 != null // Ensure absence slot is not null
                            && slot1.getSlotTime() != null // Ensure main slot time is not null
                            && slot1
                                .getSlotTime()
                                .equals(
                                    slot2
                                        .getSlotTime()) // Compare the string representation of slot
                    // times
                    ));
  }

  /**
   * Reorders the queue numbers for a list of time slots, assigning sequential numbers for each
   * shift period (MORNING, AFTERNOON, EVENING).
   *
   * <p>This method iterates through the provided list of {@link QueueTimeSlot} objects. It
   * maintains separate counters for morning, afternoon, and evening shifts. Based on the {@code
   * shiftTime} property of each slot, it assigns a new, sequential {@code slotNo}.
   *
   * @param queueTimeSlots The list of {@link QueueTimeSlot} objects to be reordered. This list is
   *     modified in place.
   */
  private static void reorderQueueNumbers(List<QueueTimeSlot> queueTimeSlots) {
    // Placeholder for inline comments to be added next
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

  /**
   * Gathers all necessary information required for generating new time slots for a specific doctor
   * at a given clinic for the current day.
   *
   * <p>This method aggregates data from multiple sources:
   *
   * <ul>
   *   <li>Doctor's general information and weekly availability schedule.
   *   <li>Specific absences recorded for the doctor at the clinic for the current date.
   *   <li>Any queue time slots that might already exist for the doctor at the clinic.
   * </ul>
   *
   * The collected information is then compiled into a {@link DoctorAvailabilityInformation} object.
   * This object includes details like the doctor's name, ID, clinic ID, their availability shifts
   * filtered for the current day of the week, structured absence information for the current day,
   * and any pre-existing slots.
   *
   * @param doctorId The unique identifier for the doctor.
   * @param clinicId The unique identifier for the clinic.
   * @return A {@link DoctorAvailabilityInformation} object containing an aggregation of the
   *     doctor's availability, absences for the current day, existing slots (if any), and current
   *     date context.
   */
  @Override
  public DoctorAvailabilityInformation getDetailsForSlotCreation(
      String doctorId, Integer clinicId) {
    // Method body follows
    final DoctorInformation information =
        this.doctorInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    final List<DoctorAbsenceInformation> absenceInformation =
        this.doctorAbsenceInformationRepository.findByAbsenceDateAndClinicIdAndDoctorId(
            Date.valueOf(LocalDate.now()), clinicId, doctorId);
    final List<QueueTimeSlot> queueTimeSlots =
        this.slotInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    LOGGER.info("Doctor Availability Information: {}", information);
    List<DoctorAvailability> todayAvailability =
        this.filterShiftDetails(
            information.getDoctorAvailability(), LocalDate.now().getDayOfWeek().toString());
    LOGGER.info(
        "Doctor Availability for today ({}): {}",
        LocalDate.now().getDayOfWeek(),
        todayAvailability);
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

  /**
   * Generates or retrieves the daily time slots for a specific doctor at a given clinic for the
   * current day.
   *
   * <p>Overall goal: This method is responsible for ensuring that a complete and accurate list of
   * time slots is available for a doctor for the current day. It first checks if slots have already
   * been generated to avoid redundant processing. If not, it proceeds to generate them based on the
   * doctor's availability and absences.
   *
   * <p>Initial check for existing slots: Before any generation logic, the method queries the {@code
   * slotGenerationRepository} to see if an entry exists for the given {@code doctorId}, {@code
   * clinicId}, and the current date. If such an entry exists (indicating slots were previously
   * generated), it fetches these slots directly from the {@code slotInformationRepository} and
   * returns them, filtering for the current date.
   *
   * <p>Slot generation process (if no existing slots are found):
   *
   * <ul>
   *   <li><b>Fetch Details:</b> It calls {@link #getDetailsForSlotCreation(String, Integer)} to
   *       obtain the doctor's general availability (shifts, consultation time) and any specific
   *       absences recorded for the current day.
   *   <li><b>Iterate Shifts:</b> The method iterates through each {@link DoctorAvailability} (shift
   *       detail like MORNING, AFTERNOON, EVENING) defined for the doctor on the current day.
   *   <li><b>Consultation Time & Slot Creation:</b> Within each shift, it uses the {@code
   *       shiftDetail.getConsultationTime()} to iteratively create individual {@link QueueTimeSlot}
   *       objects. Each slot's start time is determined by adding the consultation duration to the
   *       previous slot's start time.
   *   <li><b>Absence Handling:</b> Doctor absences (both full-day and partial-day) are considered.
   *       The logic identifies absence periods. While initial slots might be created during these
   *       absence times, a subsequent call to {@link #removeDoctorAbsence(List, List)} is made.
   *       This utility method filters out any generated slots that fall within the actual absence
   *       periods specified in {@code queueAbsenceTimeSlots}.
   *   <li><b>Reorder Queue Numbers:</b> After slots are generated and absences are accounted for,
   *       {@link #reorderQueueNumbers(List)} is called to ensure that the {@code slotNo} attribute
   *       of each {@code QueueTimeSlot} is sequential within its respective shift period (e.g.,
   *       MORNING slots are numbered 1, 2, 3,...).
   *   <li><b>Batch Saving:</b> The newly created and processed {@code QueueTimeSlot} objects are
   *       saved to the {@code slotInformationRepository} in batches (defined by {@code BATCH_SIZE})
   *       to optimize database operations and manage memory.
   *   <li><b>Record Generation:</b> Finally, a {@link SlotGeneration} record is created and saved
   *       to the {@code slotGenerationRepository}. This record marks that slots have been generated
   *       for the doctor, clinic, and date, and includes the total number of slots created. This
   *       prevents re-generation on subsequent calls for the same day.
   * </ul>
   *
   * @param doctorId The unique identifier of the doctor for whom slots are to be
   *     generated/retrieved.
   * @param clinicId The unique identifier of the clinic for which the slots are relevant.
   * @return A {@link List} of {@link QueueTimeSlot} objects representing the available and ordered
   *     time slots for the doctor at the clinic for the current day. This list may be empty if the
   *     doctor has no availability or if all slots are marked as absent.
   */
  @Override
  public List<QueueTimeSlot> getTimeSlotInformation(String doctorId, Integer clinicId) {
    Date today = Date.valueOf(LocalDate.now());
    String dayOfWeek = LocalDate.now().getDayOfWeek().toString();
    LOGGER.info("Executing Scheduled Job for Today: {} : {}", dayOfWeek, today);

    // 1. Return existing slots if already generated for today
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

    // 2. Fetch Doctor availability and absence information
    DoctorAvailabilityInformation info = getDetailsForSlotCreation(doctorId, clinicId);
    List<DoctorAvailability> shiftDetails = info.getDoctorShiftAvailability().getShiftDetails();
    List<DoctorShiftAbsence> shiftAbsences = info.getDoctorShiftAvailability().getShiftAbsence();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    List<QueueTimeSlot> queueTimeSlots = new ArrayList<>();
    List<QueueTimeSlot> queueAbsenceTimeSlots = new ArrayList<>();

    for (DoctorAvailability shift : shiftDetails) {
      ShiftTime shiftTime = shift.getShiftTime();
      int consultationTime = shift.getConsultationTime();

      if (consultationTime <= 0) {
        LOGGER.warn("Skipping shift due to invalid consultation time for doctor: {}", doctorId);
        continue;
      }

      LocalTime shiftStart = shift.getShiftStartTime().toLocalTime();
      LocalTime shiftEnd = shift.getShiftEndTime().toLocalTime();

      // Only proceed if current day matches shift day
      String currentDay = LocalDate.now().getDayOfWeek().toString();
      if (!shift.getAvailableDays().name().equalsIgnoreCase(currentDay)) continue;

      // Check absence
      boolean fullDayAbsent =
          shiftAbsences.stream().anyMatch(a -> a.getShiftTime() == ShiftTime.FULL_DAY);
      if (fullDayAbsent) continue;

      List<DoctorShiftAbsence> absencesForThisShift =
          shiftAbsences.stream().filter(a -> a.getShiftTime() == shiftTime).toList();

      // Generate normal slots up to shiftEnd (handle next-day overflow)
      int slotNo = 1;
      int maxSlots = 100; // Prevent infinite loop
      int currentSlotCount = 0;

      LocalDateTime slotStart = LocalDateTime.of(LocalDate.now(), shiftStart);
      LocalDateTime slotEnd = LocalDateTime.of(LocalDate.now(), shiftEnd);
      if (shiftEnd.isBefore(shiftStart)) {
        slotEnd = slotEnd.plusDays(1); // Shift ends after midnight
      }

      while (slotStart.plusMinutes(consultationTime).isBefore(slotEnd)
          && currentSlotCount < maxSlots) {
        QueueTimeSlot slot =
            createQueueTimeSlot(clinicId, doctorId, shift, slotNo++, slotStart.toLocalTime(), true);
        queueTimeSlots.add(slot);
        slotStart = slotStart.plusMinutes(consultationTime);
        currentSlotCount++;
      }

      // Handle absence slots (to be removed later)
      for (DoctorShiftAbsence absence : absencesForThisShift) {
        LocalTime aStart = LocalTime.parse(absence.getAbsenceStartTime(), formatter);
        LocalTime aEnd = LocalTime.parse(absence.getAbsenceEndTime(), formatter);
        LocalDateTime aStartDT = LocalDateTime.of(LocalDate.now(), aStart);
        LocalDateTime aEndDT = LocalDateTime.of(LocalDate.now(), aEnd);
        if (aEnd.isBefore(aStart)) aEndDT = aEndDT.plusDays(1);

        int count = 0;
        while (aStartDT.plusMinutes(consultationTime).isBefore(aEndDT) && count++ < 100) {
          queueAbsenceTimeSlots.add(
              createQueueTimeSlot(clinicId, doctorId, shift, 0, aStartDT.toLocalTime(), false));
          aStartDT = aStartDT.plusMinutes(consultationTime);
        }
      }
    }

    removeDoctorAbsence(queueTimeSlots, queueAbsenceTimeSlots);
    reorderQueueNumbers(queueTimeSlots);

    // Batch save
    for (int i = 0; i < queueTimeSlots.size(); i += BATCH_SIZE) {
      int end = Math.min(i + BATCH_SIZE, queueTimeSlots.size());
      slotInformationRepository.saveAll(queueTimeSlots.subList(i, end));
    }

    SlotGeneration generation = new SlotGeneration();
    generation.setDoctorId(doctorId);
    generation.setClinicId(clinicId);
    generation.setSlotDate(today);
    generation.setStatus(true);
    generation.setNoOfSlots(queueTimeSlots.size());
    slotGenerationRepository.save(generation);

    return queueTimeSlots;
  }

  /**
   * Utility method to create and initialize a new {@link QueueTimeSlot} object.
   *
   * <p>This helper function populates a new {@code QueueTimeSlot} instance with essential details
   * such as clinic and doctor identifiers, the date of the slot (set to the current date), the
   * specific shift (e.g., MORNING), the sequential slot number within that shift, the exact time of
   * the slot, and its availability status.
   *
   * @param clinicId The ID of the clinic where the slot is being created.
   * @param doctorId The ID of the doctor for whom the slot is being created.
   * @param shiftDetail The {@link DoctorAvailability} object representing the current shift, used
   *     to derive the shift type (e.g., MORNING, AFTERNOON).
   * @param slotNo The sequential number assigned to this slot within its shift period.
   * @param slotTime The specific {@link LocalTime} at which this slot is scheduled.
   * @param isAvailable A boolean flag indicating if this slot is generally available (true) or
   *     explicitly unavailable (false, e.g., if it's generated to mark an absence period).
   * @return A newly instantiated and populated {@link QueueTimeSlot} object.
   */
  private QueueTimeSlot createQueueTimeSlot(
      Integer clinicId,
      String doctorId,
      DoctorAvailability shiftDetail,
      int slotNo,
      LocalTime slotTime,
      boolean isAvailable) {
    // Method body follows
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

  /**
   * Transforms a list of raw {@link DoctorAbsenceInformation} objects into a list of {@link
   * DoctorShiftAbsence} objects.
   *
   * <p>This method processes each {@code DoctorAbsenceInformation} record, which typically
   * represents an absence entry directly from a data source. It converts this raw data into a more
   * structured {@code DoctorShiftAbsence} format, suitable for use within the slot generation
   * logic. The conversion primarily involves creating a {@code DoctorShiftAbsence} object for each
   * input record using the {@link #createDoctorShiftAbsence(DoctorAbsenceInformation)} helper
   * method.
   *
   * @param absenceInformation A list of {@link DoctorAbsenceInformation} objects representing the
   *     doctor's raw absence data.
   * @return A list of {@link DoctorShiftAbsence} objects. If the input {@code absenceInformation}
   *     is null, an empty list is returned.
   */
  private List<DoctorShiftAbsence> filterAbsenceInformation(
      List<DoctorAbsenceInformation> absenceInformation) {
    // Method body follows
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

  /**
   * Converts a single {@link DoctorAbsenceInformation} object into a {@link DoctorShiftAbsence}
   * object.
   *
   * <p>This method is responsible for transforming a raw absence record into a structured format
   * that explicitly defines the absence in terms of shifts. It determines the day of the week for
   * the absence (currently assumes the absence pertains to the day the method is executed, as it
   * uses {@code LocalDate.now()}) and calculates the effective shift period (e.g., MORNING,
   * AFTERNOON, FULL_DAY) using the {@link #calculateShiftTime(Time, Time)} method based on the
   * absence's start and end times.
   *
   * @param information The {@link DoctorAbsenceInformation} object containing the details of a
   *     single absence period.
   * @return A {@link DoctorShiftAbsence} object populated with the absence day, calculated shift
   *     time, and the original start and end times of the absence.
   */
  private DoctorShiftAbsence createDoctorShiftAbsence(DoctorAbsenceInformation information) {
    // Method body follows
    DoctorShiftAbsence doctorShiftAbsence = new DoctorShiftAbsence();
    doctorShiftAbsence.setAbsenceDay(LocalDate.now().getDayOfWeek().toString());
    doctorShiftAbsence.setShiftTime(
        calculateShiftTime(information.getAbsenceStartTime(), information.getAbsenceEndTime()));
    doctorShiftAbsence.setAbsenceStartTime(String.valueOf(information.getAbsenceStartTime()));
    doctorShiftAbsence.setAbsenceEndTime(String.valueOf(information.getAbsenceEndTime()));
    return doctorShiftAbsence;
  }

  /**
   * Determines the general shift period (MORNING, AFTERNOON, EVENING, or FULL_DAY) that an absence,
   * defined by its start and end times, falls into.
   *
   * <p>The classification logic is as follows:
   *
   * <ul>
   *   <li>If the absence ends by 12:00 PM (noon), it's classified as {@link ShiftTime#MORNING}.
   *   <li>If the absence ends after 12:00 PM but by 4:00 PM (16:00), it's {@link
   *       ShiftTime#AFTERNOON}.
   *   <li>If the absence ends after 4:00 PM but by 10:00 PM (22:00), it's {@link
   *       ShiftTime#EVENING}.
   *   <li>If the absence starts exactly at midnight (00:00), it's considered {@link
   *       ShiftTime#FULL_DAY}.
   *   <li>Any other cases (e.g., an absence spanning past 10:00 PM but not starting at midnight)
   *       are defaulted to {@link ShiftTime#FULL_DAY}.
   * </ul>
   *
   * @param absenceStartTime The start {@link java.sql.Time} of the absence period.
   * @param absenceEndTime The end {@link java.sql.Time} of the absence period.
   * @return A {@link ShiftTime} enum value indicating the calculated shift period for the absence.
   */
  private ShiftTime calculateShiftTime(Time absenceStartTime, Time absenceEndTime) {
    // Method body follows
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

  /**
   * Filters a doctor's general availability list to retain only those schedules that apply to a
   * specific day of the week.
   *
   * <p>This utility method iterates through a list of {@link DoctorAvailability} objects, each
   * representing a potential work schedule for a doctor (which can span multiple days). It compares
   * the {@code availableDays} property of each schedule (an enum representing a day like MONDAY,
   * TUESDAY, etc.) with the provided {@code currentDayOfWeek} string. The comparison is
   * case-insensitive. If a schedule's {@code availableDays} matches the {@code currentDayOfWeek},
   * it is included in the returned list.
   *
   * @param shiftDetails The complete list of {@link DoctorAvailability} objects for a doctor,
   *     potentially covering various days.
   * @param currentDayOfWeek A string representing the target day of the week (e.g., "MONDAY",
   *     "tuesday") for which to filter the availability schedules.
   * @return A new list containing only those {@link DoctorAvailability} entries that are relevant
   *     to the specified {@code currentDayOfWeek}. If {@code shiftDetails} is null or if no
   *     schedules match the given day, an empty list is returned.
   */
  private List<DoctorAvailability> filterShiftDetails(
      List<DoctorAvailability> shiftDetails, String currentDayOfWeek) {
    // Method body follows
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

  /** Ensures a slot with start time and duration will not exceed shift end time. */
  private boolean slotFitsInShift(LocalTime slotStart, int durationMinutes, LocalTime shiftEnd) {
    return !slotStart.plusMinutes(durationMinutes).isAfter(shiftEnd);
  }
}
