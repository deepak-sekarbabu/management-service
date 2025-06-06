package com.deepak.management.repository;

import com.deepak.queue.model.SlotGeneration;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotGenerationRepository extends JpaRepository<SlotGeneration, Integer> {
  Optional<SlotGeneration> findByDoctorIdAndClinicIdAndSlotDate(
      String doctorId, Integer clinicId, Date slotDate);

  List<SlotGeneration> findBySlotDateAndDoctorIdAndClinicId(
      Date slotDate, String doctorId, Integer clinicId);
}
