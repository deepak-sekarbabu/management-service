package com.deepak.management.repository;

import com.deepak.management.queue.model.SlotGeneration;
import java.sql.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotGenerationRepository extends JpaRepository<SlotGeneration, Integer> {

  List<SlotGeneration> findBySlotDateAndDoctorIdAndClinicId(
      Date slotDate, String doctorId, Integer clinicId);
}
