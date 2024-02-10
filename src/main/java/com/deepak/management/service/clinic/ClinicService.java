package com.deepak.management.service.clinic;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.model.clinic.ClinicInformation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ClinicService {

    List<ClinicInformation> getAllClinics(int page, int size);

    Optional<ClinicInformation> getClinicById(Integer clinicId) throws ClinicNotFound;

    ClinicInformation updateClinic(Integer clinicId, ClinicInformation clinicInformation) throws ClinicNotFound;
}