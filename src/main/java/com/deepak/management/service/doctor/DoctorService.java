package com.deepak.management.service.doctor;

import com.deepak.management.exception.ClinicNotFound;
import com.deepak.management.exception.DoctorNotFound;
import com.deepak.management.model.doctor.DoctorInformation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface DoctorService {

  List<DoctorInformation> getDoctorInformation(Pageable page);

  Optional<DoctorInformation> getDoctorById(Long doctorId) throws DoctorNotFound;

  DoctorInformation updateDoctor(
      @PathVariable Long doctorId, @RequestBody DoctorInformation doctorInformation)
      throws ClinicNotFound, DoctorNotFound;

  void deleteDoctor(@PathVariable Long doctorId) throws DoctorNotFound;

  List<DoctorInformation> getDoctorInformationByClinicId(Integer clinicId);
}
