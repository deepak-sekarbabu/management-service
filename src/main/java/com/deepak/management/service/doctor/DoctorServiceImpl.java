package com.deepak.management.service.doctor;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.model.clinic.ClinicInformation;
import com.deepak.management.model.doctor.DoctorInformation;
import com.deepak.management.repository.ClinicInformationRepository;
import com.deepak.management.repository.DoctorInformationRepository;
import com.deepak.management.service.doctorabsence.DoctorAbsenceServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {
    private final DoctorInformationRepository doctorInformationRepository;
    private final ClinicInformationRepository clinicInformationRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorServiceImpl.class);

    public DoctorServiceImpl(DoctorInformationRepository doctorInformationRepository, ClinicInformationRepository clinicInformationRepository) {
        this.doctorInformationRepository = doctorInformationRepository;
        this.clinicInformationRepository = clinicInformationRepository;
    }

    @Override
    public List<DoctorInformation> getDoctorInformation(Pageable page) {

        Page<DoctorInformation> pagedResult = this.doctorInformationRepository.findAll(page);

        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<DoctorInformation> getDoctorById(Long doctorId) throws DoctorNotFound {
        Optional<DoctorInformation> doctor = this.doctorInformationRepository.findById(doctorId);
        if (doctor.isPresent()) {
            return doctor;
        } else {
            throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
        }
    }

    @Override
    public DoctorInformation updateDoctor(Long doctorId, DoctorInformation doctorInformation) throws ClinicNotFound, DoctorNotFound {
        Optional<DoctorInformation> doctor = this.doctorInformationRepository.findById(doctorId);
        if (doctor.isPresent()) {
            // Check if the clinicIdToUpdate exists in the clinic_information table
            Optional<ClinicInformation> clinic = clinicInformationRepository.findById(doctorInformation.getClinicId());
            if (clinic.isPresent()) {
                // Update doctor's information with the provided clinicId
                doctor.get().setClinicId(doctorInformation.getClinicId());
                doctor.get().setDoctorName(doctorInformation.getDoctorName());
                doctor.get().setPhoneNumbers(doctorInformation.getPhoneNumbers());
                doctor.get().setDoctorName(doctorInformation.getDoctorName());
                doctor.get().setDoctorAvailability(doctorInformation.getDoctorAvailability());
                doctor.get().setDoctorSpeciality(doctorInformation.getDoctorSpeciality());
                doctor.get().setDoctorExperience(doctorInformation.getDoctorExperience());
                doctor.get().setDoctorConsultationFee(doctorInformation.getDoctorConsultationFee());
                LOGGER.info("Updated doctor information for the Id : {}",doctorId);
                return this.doctorInformationRepository.save(doctor.get());
            } else {
                throw new ClinicNotFound("Clinic with id " + doctorInformation.getClinicId() + " not found");
            }
        } else {
            throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
        }
    }

    @Override
    public void deleteDoctor(Long doctorId) throws DoctorNotFound {

        if (!this.doctorInformationRepository.existsById(doctorId)) {
            throw new DoctorNotFound("Doctor with id " + doctorId + " not found");
        }
        LOGGER.warn("Deleted doctor information for the Id : {}",doctorId);
        this.doctorInformationRepository.deleteById(doctorId);
    }
}