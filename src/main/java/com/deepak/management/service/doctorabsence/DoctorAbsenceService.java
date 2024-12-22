package com.deepak.management.service.doctorabsence;

import com.deepak.management.exception.DoctorAbsenceNotFound;
import com.deepak.management.model.doctor.DoctorAbsenceInformation;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface DoctorAbsenceService {

  List<DoctorAbsenceInformation> getDoctorAbsenceInformations(Pageable pageable);

  Optional<DoctorAbsenceInformation> getDoctorAbsenceInformationsById(Long id);

  List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDate(Pageable paging, Date date);

  List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndClinic(
      Pageable paging, Date date, Integer clinicId);

  List<DoctorAbsenceInformation> getDoctorAbsenceInformationsByDateAndDoctor(
      Pageable paging, Date date, String doctorId);

  Optional<DoctorAbsenceInformation> updateDoctorAbsenceInformationById(
      Long id, DoctorAbsenceInformation doctorAbsenceInformation) throws DoctorAbsenceNotFound;

  void deleteDoctorAbsenceInfoById(Long id) throws DoctorAbsenceNotFound;

  List<DoctorAbsenceInformation> getDoctorAbsenceInformationsAfterDateAndClinic(
      Date startDate, Integer clinicId, Pageable page);

  List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndClinic(
      Date startDate, Date endDate, Integer clinicId, Pageable page);

  List<DoctorAbsenceInformation> getDoctorAbsenceInformationsBetweenDateAndDoctor(
      Date startDate, Date endDate, String doctorId, Pageable page);
}
