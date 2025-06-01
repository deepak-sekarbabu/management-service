package com.deepak.management.service.doctorabsence;

import com.deepak.management.exception.DoctorAbsenceNotFound;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import com.deepak.management.repository.DoctorAbsenceInformationRepository;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DoctorAbsenceServiceImpl implements DoctorAbsenceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoctorAbsenceServiceImpl.class);

  private final DoctorAbsenceInformationRepository doctorAbsenceInformationRepository;

  public DoctorAbsenceServiceImpl(
      DoctorAbsenceInformationRepository doctorAbsenceInformationRepository) {
    this.doctorAbsenceInformationRepository = doctorAbsenceInformationRepository;
  }

  @Override
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformations(Pageable pageable) {
    final Page<DoctorAbsenceInformation> pagedResult =
        this.doctorAbsenceInformationRepository.findAll(pageable);

    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<DoctorAbsenceInformation> getDoctorAbsenceInformationsById(Long id) {
    return this.doctorAbsenceInformationRepository.findById(id);
  }

  @Override
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDate(
      Pageable paging, Date date) {
    final Page<DoctorAbsenceInformation> pagedResult =
        this.doctorAbsenceInformationRepository.findByAbsenceDate(date, paging);
    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndClinic(
      Pageable paging, Date date, Integer clinicId) {
    final Page<DoctorAbsenceInformation> pagedResult =
        this.doctorAbsenceInformationRepository.findByAbsenceDateAndClinicId(
            date, clinicId, paging);
    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndDoctor(
      Pageable paging, Date date, String doctorId) {
    final Page<DoctorAbsenceInformation> pagedResult =
        this.doctorAbsenceInformationRepository.findByAbsenceDateAndDoctorId(
            date, doctorId, paging);
    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * Updates existing doctor absence information for the given ID.
   *
   * <p>This method attempts to parse the absence date from the input {@code
   * doctorAbsenceInformation} assuming the format "dd-MM-yyyy". If parsing is successful, the date
   * is converted to {@code java.sql.Date}. If a {@code ParseException} occurs during this process,
   * the error is logged, and the method proceeds using the original {@code sqlDate} (which might be
   * null or retain a previous value if the field was already populated).
   *
   * @param id The ID of the doctor absence information to update.
   * @param doctorAbsenceInformation The new doctor absence information. The {@code
   *     doctorAbsenceInformation.getAbsenceDate()} is expected to be in "dd-MM-yyyy" format.
   * @return An {@code Optional} containing the updated {@code DoctorAbsenceInformation} if the
   *     update was successful.
   * @throws DoctorAbsenceNotFound if no doctor absence information is found for the given ID.
   */
  @Override
  public Optional<DoctorAbsenceInformation> updateDoctorAbsenceInformationById(
      Long id, DoctorAbsenceInformation doctorAbsenceInformation) throws DoctorAbsenceNotFound {
    final Optional<DoctorAbsenceInformation> existingInfoOptional =
        this.getDoctorAbsenceInformationsById(id);

    if (existingInfoOptional.isPresent()) {
      final DoctorAbsenceInformation existingInfo = existingInfoOptional.get();

      SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
      final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
      Date sqlDate = null;
      try {
        final java.util.Date date = inputFormat.parse(doctorAbsenceInformation.getAbsenceDate());
        final String formattedDate = outputFormat.format(date);
        sqlDate = Date.valueOf(formattedDate);
      } catch (ParseException ignored) {
        LOGGER.error("Error in parsing date while updating doctor absence information");
      }
      existingInfo.setAbsenceDate(sqlDate);
      existingInfo.setDoctorName(doctorAbsenceInformation.getDoctorName());
      existingInfo.setAbsenceStartTime(doctorAbsenceInformation.getAbsenceStartTime());
      existingInfo.setAbsenceEndTime(doctorAbsenceInformation.getAbsenceEndTime());
      existingInfo.setClinicId(doctorAbsenceInformation.getClinicId());
      existingInfo.setDoctorId(doctorAbsenceInformation.getDoctorId());
      existingInfo.setOptionalMessage(doctorAbsenceInformation.getOptionalMessage());
      LOGGER.info("Updated absence info with id: {}", existingInfo.getId());
      return Optional.of(doctorAbsenceInformationRepository.save(existingInfo));
    } else {
      LOGGER.error("Doctor Absence Information not found with ID: {}", id);
      throw new DoctorAbsenceNotFound("Doctor Absence Information not found with ID: " + id);
    }
  }

  @Override
  public void deleteDoctorAbsenceInfoById(Long id) throws DoctorAbsenceNotFound {
    final Optional<DoctorAbsenceInformation> absenceInformation =
        this.getDoctorAbsenceInformationsById(id);
    if (absenceInformation.isEmpty()) {
      LOGGER.warn("Attempt to delete a doctor information that does not exist");
      throw new DoctorAbsenceNotFound(
          "Doctor Absence Information with ID " + id + " does not exist.");
    }
    LOGGER.warn("Deleted doctor absence info with id: {}", id);
    doctorAbsenceInformationRepository.deleteById(id);
  }

  @Override
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsAfterDateAndClinic(
      Date absenceDate, Integer clinicId, Pageable page) {
    final Page<DoctorAbsenceInformation> pagedResult =
        doctorAbsenceInformationRepository.findByAbsenceDateGreaterThanEqualAndClinicId(
            absenceDate, clinicId, page);
    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndClinic(
      Date startDate, Date endDate, Integer clinicId, Pageable page) {
    final Page<DoctorAbsenceInformation> pagedResult =
        doctorAbsenceInformationRepository.findByAbsenceDateBetweenAndClinicId(
            startDate, endDate, clinicId, page);
    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndDoctor(
      Date startDate, Date endDate, String doctorId, Pageable page) {
    final Page<DoctorAbsenceInformation> pagedResult =
        doctorAbsenceInformationRepository.findByAbsenceDateBetweenAndDoctorId(
            startDate, endDate, doctorId, page);
    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }
}
