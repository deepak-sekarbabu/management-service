package com.deepak.management.repository;

import com.deepak.management.queue.model.QueueTimeSlot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotInformationRepository extends JpaRepository<QueueTimeSlot, Long> {

  List<QueueTimeSlot> findByDoctorIdAndClinicId(String doctorId, Integer clinicId);
}
