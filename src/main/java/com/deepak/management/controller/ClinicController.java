package com.deepak.management.controller;

import com.deepak.management.exception.ClinicNotFoundException;
import com.deepak.management.model.ClinicInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clinics")
public class ClinicController {

    @Autowired
    private ClinicInformationRepository clinicInformationRepository;

    @GetMapping
    public List<ClinicInformation> getAllClinics(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);

        Page<ClinicInformation> pagedResult = this.clinicInformationRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<ClinicInformation>();
        }
    }

    @GetMapping("/{clinicId}")
    public Optional<ClinicInformation> getClinicById(@PathVariable Integer clinicId) {
        return this.clinicInformationRepository.findById(clinicId);
    }

    @PostMapping
    public ClinicInformation saveClinic(@RequestBody ClinicInformation clinic) {
        return this.clinicInformationRepository.save(clinic);
    }

    @PutMapping("/{clinicId}")
    public ClinicInformation updateClinic(@PathVariable Integer clinicId, @RequestBody ClinicInformation clinic) throws ClinicNotFoundException {
        Optional<ClinicInformation> existingClinic = this.clinicInformationRepository.findById(clinicId);
        if (existingClinic.isPresent()) {
            existingClinic.get().setClinicName(clinic.getClinicName());
            existingClinic.get().setClinicAddress(clinic.getClinicAddress());
            existingClinic.get().setClinicPinCode(clinic.getClinicPinCode());
            existingClinic.get().setMapGeoLocation(clinic.getMapGeoLocation());
            existingClinic.get().setClinicPhoneNumbers(clinic.getClinicPhoneNumbers());
            existingClinic.get().setNoOfDoctors(clinic.getNoOfDoctors());
            return this.clinicInformationRepository.save(existingClinic.get());
        } else {
            throw new ClinicNotFoundException("Clinic with id " + clinicId + " not found");
        }
    }


    @DeleteMapping("/{clinicId}")
    public void deleteClinic(@PathVariable Integer clinicId) throws ClinicNotFoundException {

        if (!this.clinicInformationRepository.existsById(clinicId)) {
            throw new ClinicNotFoundException("Clinic with id " + clinicId + " not found");
        }

        this.clinicInformationRepository.deleteById(clinicId);

    }
}
