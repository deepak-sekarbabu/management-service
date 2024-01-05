package com.deepak.management.service.clinic;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.model.clinic.ClinicInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClinicServiceImpl implements ClinicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicServiceImpl.class);
    private final ClinicInformationRepository clinicInformationRepository;

    public ClinicServiceImpl(ClinicInformationRepository clinicInformationRepository) {
        this.clinicInformationRepository = clinicInformationRepository;
    }

    @Override
    public List<ClinicInformation> getAllClinics(int page, int size) {
        Pageable paging = PageRequest.of(page, size);

        Page<ClinicInformation> pagedResult = this.clinicInformationRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<ClinicInformation> getClinicById(Integer clinicId) throws ClinicNotFound {
        Optional<ClinicInformation> existingClinic = this.clinicInformationRepository.findById(clinicId);
        if (existingClinic.isPresent()) {
            return existingClinic;
        } else {
            throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
        }
    }

    @Override
    public ClinicInformation updateClinic(Integer clinicId, ClinicInformation clinicInformation) throws ClinicNotFound {
        Optional<ClinicInformation> existingClinic = this.clinicInformationRepository.findById(clinicId);
        if (existingClinic.isPresent()) {
            existingClinic.get().setClinicName(clinicInformation.getClinicName());
            existingClinic.get().setClinicAddress(clinicInformation.getClinicAddress());
            existingClinic.get().setClinicPinCode(clinicInformation.getClinicPinCode());
            existingClinic.get().setMapGeoLocation(clinicInformation.getMapGeoLocation());
            existingClinic.get().setClinicPhoneNumbers(clinicInformation.getClinicPhoneNumbers());
            existingClinic.get().setNoOfDoctors(clinicInformation.getNoOfDoctors());
            existingClinic.get().setClinicEmail(clinicInformation.getClinicEmail());
            existingClinic.get().setClinicTimings(clinicInformation.getClinicTimings());
            existingClinic.get().setClinicAmenities(clinicInformation.getClinicAmenities());
            existingClinic.get().setClinicWebsite(clinicInformation.getClinicWebsite());
            LOGGER.info("Updated Clinic information for clinic id {}", clinicId);
            return this.clinicInformationRepository.save(existingClinic.get());
        } else {
            throw new ClinicNotFound("Clinic with id " + clinicId + " not found");
        }
    }
}