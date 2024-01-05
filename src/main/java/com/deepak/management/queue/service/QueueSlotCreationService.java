package com.deepak.management.queue.service;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface QueueSlotCreationService {

    List<Object[]> getDetailsForSlotCreation(String doctorId, String clinicId);
}
