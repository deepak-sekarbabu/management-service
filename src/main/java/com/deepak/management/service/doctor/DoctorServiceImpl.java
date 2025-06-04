package com.deepak.management.service.doctor;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.model.clinic.ClinicInformation;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import com.deepak.management.repository.DoctorInformationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class DoctorServiceImpl implements DoctorService {
  private static final Logger LOGGER = LoggerFactory.getLogger(DoctorServiceImpl.class);
  private final DoctorInformationRepository doctorInformationRepository;
  private final ClinicInformationRepository clinicInformationRepository;

  public DoctorServiceImpl(
      DoctorInformationRepository doctorInformationRepository,
      ClinicInformationRepository clinicInformationRepository) {
    this.doctorInformationRepository = doctorInformationRepository;
    this.clinicInformationRepository = clinicInformationRepository;
  }

  @Override
  public List<DoctorInformation> getDoctorInformation(Pageable page) {

    final Page<DoctorInformation> pagedResult = this.doctorInformationRepository.findAll(page);

    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<DoctorInformation> getDoctorById(Long doctorId) throws DoctorNotFound {
    final Optional<DoctorInformation> doctor = this.doctorInformationRepository.findById(doctorId);
    if (doctor.isPresent()) {
      return doctor;
    } else {
      throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
    }
  }

  @Override
  public Optional<DoctorInformation> getDoctorByDoctorIdAndClinicId(
      String doctorId, Integer clinicId) throws DoctorNotFound {
    DoctorInformation doctor =
        this.doctorInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    if (doctor != null) {
      return Optional.of(doctor);
    } else {
      throw new DoctorNotFound(
          "Doctor with doctorId " + doctorId + " and clinicId " + clinicId + " not found");
    }
  }

  @Override
  public DoctorInformation updateDoctor(Long doctorId, DoctorInformation doctorInformation)
      throws ClinicNotFound, DoctorNotFound {
    final Optional<DoctorInformation> doctor = this.doctorInformationRepository.findById(doctorId);
    if (doctor.isPresent()) {
      if (doctorInformation.getClinicId() != null) {
        // Check if the clinicIdToUpdate exists in the clinic_information table
        final Optional<ClinicInformation> clinic =
            clinicInformationRepository.findById(doctorInformation.getClinicId());
        if (clinic.isPresent()) {
          doctor.get().setClinicId(doctorInformation.getClinicId());
        } else {
          throw new ClinicNotFound(
              "Clinic with id " + doctorInformation.getClinicId() + " not found");
        }
      }
      if (doctorInformation.getDoctorName() != null) {
        doctor.get().setDoctorName(doctorInformation.getDoctorName());
      }
      if (doctorInformation.getPhoneNumbers() != null) {
        doctor.get().setPhoneNumbers(doctorInformation.getPhoneNumbers());
      }
      if (doctorInformation.getDoctorEmail() != null) {
        doctor.get().setDoctorEmail(doctorInformation.getDoctorEmail());
      }
      if (doctorInformation.getGender() != null) {
        doctor.get().setGender(doctorInformation.getGender());
      }
      if (doctorInformation.getDoctorAvailability() != null) {
        doctor.get().setDoctorAvailability(doctorInformation.getDoctorAvailability());
      }
      if (doctorInformation.getDoctorSpeciality() != null) {
        doctor.get().setDoctorSpeciality(doctorInformation.getDoctorSpeciality());
      }
      if (doctorInformation.getDoctorExperience() != null) {
        doctor.get().setDoctorExperience(doctorInformation.getDoctorExperience());
      }
      if (doctorInformation.getDoctorConsultationFee() != null) {
        doctor.get().setDoctorConsultationFee(doctorInformation.getDoctorConsultationFee());
      }
      if (doctorInformation.getDoctorConsultationFeeOther() != null) {
        doctor
            .get()
            .setDoctorConsultationFeeOther(doctorInformation.getDoctorConsultationFeeOther());
      }
      if (doctorInformation.getLanguagesSpoken() != null) {
        doctor.get().setLanguagesSpoken(doctorInformation.getLanguagesSpoken());
      }
      if (doctorInformation.getQualifications() != null) {
        doctor.get().setQualifications(doctorInformation.getQualifications());
      }
      LOGGER.info("Updated doctor information for the Id : {}", doctorId);
      return this.doctorInformationRepository.save(doctor.get());
    } else {
      throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
    }
  }

  @Override
  public DoctorInformation updateDoctorByDoctorIdAndClinicId(
      String doctorId, Integer clinicId, DoctorInformation doctorInformation)
      throws ClinicNotFound, DoctorNotFound {
    DoctorInformation doctor =
        this.doctorInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    if (doctor != null) {
      if (doctorInformation.getClinicId() != null) {
        final Optional<ClinicInformation> clinic =
            clinicInformationRepository.findById(doctorInformation.getClinicId());
        if (clinic.isPresent()) {
          doctor.setClinicId(doctorInformation.getClinicId());
        } else {
          throw new ClinicNotFound(
              "Clinic with id " + doctorInformation.getClinicId() + " not found");
        }
      }
      if (doctorInformation.getDoctorName() != null) {
        doctor.setDoctorName(doctorInformation.getDoctorName());
      }
      if (doctorInformation.getPhoneNumbers() != null) {
        doctor.setPhoneNumbers(doctorInformation.getPhoneNumbers());
      }
      if (doctorInformation.getDoctorEmail() != null) {
        doctor.setDoctorEmail(doctorInformation.getDoctorEmail());
      }
      if (doctorInformation.getGender() != null) {
        doctor.setGender(doctorInformation.getGender());
      }
      if (doctorInformation.getDoctorAvailability() != null) {
        doctor.setDoctorAvailability(doctorInformation.getDoctorAvailability());
      }
      if (doctorInformation.getDoctorSpeciality() != null) {
        doctor.setDoctorSpeciality(doctorInformation.getDoctorSpeciality());
      }
      if (doctorInformation.getDoctorExperience() != null) {
        doctor.setDoctorExperience(doctorInformation.getDoctorExperience());
      }
      if (doctorInformation.getDoctorConsultationFee() != null) {
        doctor.setDoctorConsultationFee(doctorInformation.getDoctorConsultationFee());
      }
      if (doctorInformation.getDoctorConsultationFeeOther() != null) {
        doctor.setDoctorConsultationFeeOther(doctorInformation.getDoctorConsultationFeeOther());
      }
      if (doctorInformation.getLanguagesSpoken() != null) {
        doctor.setLanguagesSpoken(doctorInformation.getLanguagesSpoken());
      }
      if (doctorInformation.getQualifications() != null) {
        doctor.setQualifications(doctorInformation.getQualifications());
      }
      LOGGER.info(
          "Updated doctor information for doctorId: {} and clinicId: {}", doctorId, clinicId);
      return this.doctorInformationRepository.save(doctor);
    } else {
      throw new DoctorNotFound(
          "Doctor with doctorId " + doctorId + " and clinicId " + clinicId + " not found");
    }
  }

  @Override
  public void deleteDoctor(Long doctorId) throws DoctorNotFound {

    if (!this.doctorInformationRepository.existsById(doctorId)) {
      throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
    }
    LOGGER.warn("Deleted doctor information for the Id : {}", doctorId);
    this.doctorInformationRepository.deleteById(doctorId);
  }

  @Override
  public void deleteDoctorByDoctorIdAndClinicId(String doctorId, Integer clinicId)
      throws DoctorNotFound {
    DoctorInformation doctor =
        this.doctorInformationRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    if (doctor == null) {
      throw new DoctorNotFound(
          "Doctor with doctorId " + doctorId + " and clinicId " + clinicId + " not found");
    }
    LOGGER.warn("Deleted doctor information for doctorId: {} and clinicId: {}", doctorId, clinicId);
    this.doctorInformationRepository.delete(doctor);
  }

  @Override
  public List<DoctorInformation> getDoctorInformationByClinicId(Integer clinicId) {
    LOGGER.warn("getDoctorInformationByClinicId Id : {}", clinicId);
    return this.doctorInformationRepository.findAllByClinicId(clinicId);
  }
}
