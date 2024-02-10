package com.deepak.management.repository;

import com.deepak.management.queue.model.SlotGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotGenerationRepository extends JpaRepository<SlotGeneration, Integer> {

}