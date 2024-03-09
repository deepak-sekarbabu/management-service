package com.deepak.management.repository;

import com.deepak.management.queue.model.QueueTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotInformationRepository extends JpaRepository<QueueTimeSlot, Long> {
}
