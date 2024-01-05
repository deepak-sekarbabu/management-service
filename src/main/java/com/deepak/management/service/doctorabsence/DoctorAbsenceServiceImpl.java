package com.deepak.management.service.doctorabsence;

import com.deepak.management.exception.DoctorAbsenceNotFound;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import com.deepak.management.repository.DoctorAbsenceInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorAbsenceServiceImpl implements DoctorAbsenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorAbsenceServiceImpl.class);

    private final DoctorAbsenceInformationRepository doctorAbsenceInformationRepository;

    public DoctorAbsenceServiceImpl(DoctorAbsenceInformationRepository doctorAbsenceInformationRepository) {
        this.doctorAbsenceInformationRepository = doctorAbsenceInformationRepository;
    }

    @Override
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformations(Pageable pageable) {
        Page<DoctorAbsenceInformation> pagedResult = this.doctorAbsenceInformationRepository.findAll(pageable);

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
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDate(Pageable paging, Date date) {
        Page<DoctorAbsenceInformation> pagedResult = this.doctorAbsenceInformationRepository.findByAbsenceDate(date, paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndClinic(Pageable paging, Date date, Integer clinicId) {
        Page<DoctorAbsenceInformation> pagedResult = this.doctorAbsenceInformationRepository.findByAbsenceDateAndClinicId(date, clinicId, paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndDoctor(Pageable paging, Date date, String doctorId) {
        Page<DoctorAbsenceInformation> pagedResult = this.doctorAbsenceInformationRepository.findByAbsenceDateAndDoctorId(date, doctorId, paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<DoctorAbsenceInformation> updateDoctorAbsenceInformationById(Long id, DoctorAbsenceInformation doctorAbsenceInformation) throws DoctorAbsenceNotFound {
        Optional<DoctorAbsenceInformation> existingInfoOptional = this.getDoctorAbsenceInformationsById(id);

        if (existingInfoOptional.isPresent()) {
            DoctorAbsenceInformation existingInfo = existingInfoOptional.get();

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date sqlDate = null;
            try {
                java.util.Date date = inputFormat.parse(doctorAbsenceInformation.getAbsenceDate());
                String formattedDate = outputFormat.format(date);
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
        Optional<DoctorAbsenceInformation> absenceInformation = this.getDoctorAbsenceInformationsById(id);
        if (!absenceInformation.isPresent()) {
            LOGGER.warn("Attempt to delete a doctor information that does not exist");
            throw new DoctorAbsenceNotFound("Doctor Absence Information with ID " + id + " does not exist.");
        }
        LOGGER.warn("Deleted doctor absence info with id: {}", id);
        doctorAbsenceInformationRepository.deleteById(id);
    }

    @Override
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndClinic(Date startDate, Date endDate, Integer clinicId, Pageable page) {
        Page<DoctorAbsenceInformation> pagedResult = doctorAbsenceInformationRepository.findByAbsenceDateBetweenAndClinicId(startDate, endDate, clinicId, page);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndDoctor(Date startDate, Date endDate, String doctorId, Pageable page) {
        Page<DoctorAbsenceInformation> pagedResult = doctorAbsenceInformationRepository.findByAbsenceDateBetweenAndDoctorId(startDate, endDate, doctorId, page);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>();
        }
    }
}