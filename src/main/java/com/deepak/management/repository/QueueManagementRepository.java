package com.deepak.management.repository;

import com.deepak.management.model.queuemanagement.QueueManagementDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QueueManagementRepository {

    private EntityManager entityManager;

    public QueueManagementRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<QueueManagementDTO> getQueueManagementData() {
        String sql = "SELECT " +
                "q.queue_management_id as id, " +
                "CONCAT('/assets/images/avatars/avatar_', FLOOR(RAND() * 24) + 1, '.jpg') AS avatarUrl," +
                "u.name AS patientName, " +
                "u.phoneNumber AS patientPhoneNumber, " +
                "d.doctor_name AS doctorName, " +
                "q.current_queue_no AS queueNo, " +
                "CASE WHEN q.patient_reached = 0 THEN 'false' ELSE 'true' END AS patientReached," +
                "s.slot_time AS time," +
                "s.shift_time AS shiftTime " +
                "FROM queue_management q " +
                "JOIN appointments a ON q.appointment_id = a.appointment_id " +
                "JOIN users u ON a.user_id = u.id " +
                "JOIN doctor_information d ON q.doctor_id = d.doctor_id AND q.clinic_id = d.clinic_id " +
                "JOIN slot_information s ON q.slot_id = s.slot_id "+
                "ORDER BY s.slot_time,s.shift_time ";

        Query query = entityManager.createNativeQuery(sql, QueueManagementDTO.class);

        return query.getResultList();
    }
}
