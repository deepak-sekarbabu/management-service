package com.deepak.management.service.clinic;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.model.clinic.ClinicInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClinicServiceImpl implements ClinicService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClinicServiceImpl.class);
  private final ClinicInformationRepository clinicInformationRepository;

  public ClinicServiceImpl(ClinicInformationRepository clinicInformationRepository) {
    this.clinicInformationRepository = clinicInformationRepository;
  }

  @Override
  public List<ClinicInformation> getAllClinics(int page, int size) {
    final Pageable paging = PageRequest.of(page, size);

    final Page<ClinicInformation> pagedResult = this.clinicInformationRepository.findAll(paging);

    if (pagedResult.hasContent()) {
      return pagedResult.getContent();
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public Optional<ClinicInformation> getClinicById(Integer clinicId) throws ClinicNotFound {
    final Optional<ClinicInformation> existingClinic =
        this.clinicInformationRepository.findById(clinicId);
    if (existingClinic.isPresent()) {
      return existingClinic;
    } else {
      throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
    }
  }

  @Override
  public ClinicInformation updateClinic(Integer clinicId, ClinicInformation clinicInformation)
      throws ClinicNotFound {
    final Optional<ClinicInformation> existingClinic =
        this.clinicInformationRepository.findById(clinicId);
    if (existingClinic.isPresent()) {
      if (clinicInformation.getClinicName() != null) {
        existingClinic.get().setClinicName(clinicInformation.getClinicName());
      }
      if (clinicInformation.getClinicAddress() != null) {
        existingClinic.get().setClinicAddress(clinicInformation.getClinicAddress());
      }
      if (clinicInformation.getClinicPinCode() != null) {
        existingClinic.get().setClinicPinCode(clinicInformation.getClinicPinCode());
      }
      if (clinicInformation.getMapGeoLocation() != null) {
        existingClinic.get().setMapGeoLocation(clinicInformation.getMapGeoLocation());
      }
      if (clinicInformation.getClinicPhoneNumbers() != null) {
        existingClinic.get().setClinicPhoneNumbers(clinicInformation.getClinicPhoneNumbers());
      }
      if (clinicInformation.getNoOfDoctors() != null) {
        existingClinic.get().setNoOfDoctors(clinicInformation.getNoOfDoctors());
      }
      if (clinicInformation.getClinicEmail() != null) {
        existingClinic.get().setClinicEmail(clinicInformation.getClinicEmail());
      }
      if (clinicInformation.getClinicTimings() != null) {
        existingClinic.get().setClinicTimings(clinicInformation.getClinicTimings());
      }
      if (clinicInformation.getClinicAmenities() != null) {
        existingClinic.get().setClinicAmenities(clinicInformation.getClinicAmenities());
      }
      if (clinicInformation.getClinicWebsite() != null) {
        existingClinic.get().setClinicWebsite(clinicInformation.getClinicWebsite());
      }
      LOGGER.info("Updated Clinic information for clinic id {}", clinicId);
      return this.clinicInformationRepository.save(existingClinic.get());
    } else {
      throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
    }
  }
}
