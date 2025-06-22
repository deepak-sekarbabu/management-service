package com.deepak.management.repository;

import com.deepak.management.model.queuemanagement.QueueManagementDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class QueueManagementRepository {

  private final EntityManager entityManager;

  public QueueManagementRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public List<QueueManagementDTO> getQueueManagementData() {
    String sql =
        "SELECT "
            + "q.queue_management_id as id, "
            + "CONCAT('/assets/images/avatars/avatar_', FLOOR(RAND() * 24) + 1, '.jpg') AS avatarUrl,"
            + "JSON_UNQUOTE(JSON_EXTRACT(p.personalDetails, '$.name')) AS patientName, "
            + "p.phone_number AS patientPhoneNumber, "
            + "d.doctor_name AS doctorName, "
            + "q.current_queue_no AS queueNo, "
            + "CASE WHEN q.patient_reached = 0 THEN 'false' ELSE 'true' END AS patientReached,"
            + "TIME_FORMAT(s.slot_time, '%H:%i:%s') AS time,"
            + "TIME_FORMAT(s.shift_time, '%H:%i:%s') AS shiftTime "
            + "FROM queue_management q "
            + "JOIN appointments a ON q.appointment_id = a.appointment_id "
            + "JOIN patients p ON a.patient_id = p.id "
            + "JOIN doctor_information d ON q.doctor_id = d.doctor_id AND q.clinic_id = d.clinic_id "
            + "JOIN slot_information s ON q.slot_id = s.slot_id "
            + "ORDER BY s.slot_time,s.shift_time ";

    Query query = entityManager.createNativeQuery(sql, QueueManagementDTO.class);
    return query.getResultList();
  }

  public List<QueueManagementDTO> getQueueManagementData(String clinicId, String doctorId) {
    String sql =
        "SELECT "
            + "q.queue_management_id as id, "
            + "CONCAT('/assets/images/avatars/avatar_', FLOOR(RAND() * 24) + 1, '.jpg') AS avatarUrl,"
            + "JSON_UNQUOTE(JSON_EXTRACT(p.personalDetails, '$.name')) AS patientName, "
            + "p.phone_number AS patientPhoneNumber, "
            + "d.doctor_name AS doctorName, "
            + "q.current_queue_no AS queueNo, "
            + "CASE WHEN q.patient_reached = 0 THEN 'false' ELSE 'true' END AS patientReached,"
            + "TIME_FORMAT(s.slot_time, '%H:%i:%s') AS time,"
            + "TIME_FORMAT(s.shift_time, '%H:%i:%s') AS shiftTime "
            + "FROM queue_management q "
            + "JOIN appointments a ON q.appointment_id = a.appointment_id "
            + "JOIN patients p ON a.patient_id = p.id "
            + "JOIN doctor_information d ON q.doctor_id = d.doctor_id AND q.clinic_id = d.clinic_id "
            + "JOIN slot_information s ON q.slot_id = s.slot_id "
            + "WHERE q.clinic_id = :clinicId AND q.doctor_id = :doctorId "
            + "AND q.deleted = 0 "
            + "AND q.cancelled = 0 "
            + "ORDER BY s.slot_time, s.shift_time ";

    Query query = entityManager.createNativeQuery(sql, QueueManagementDTO.class);
    query.setParameter("clinicId", clinicId);
    query.setParameter("doctorId", doctorId);

    return query.getResultList();
  }

  // Rest of the file remains the same...

  @Transactional
  public void updatePatientReached(Integer id) {
    String sql = "UPDATE queue_management SET patient_reached = 1 WHERE queue_management_id = :id";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("id", id);
    query.executeUpdate();
  }

  @Transactional
  public void updatePatientCancelled(Integer id) {
    String sql = "UPDATE queue_management SET cancelled = 1 WHERE queue_management_id = :id";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("id", id);
    query.executeUpdate();
  }

  @Transactional
  public void updatePatientVisited(Integer id) {
    String sql =
        "UPDATE queue_management SET visit_status = 'Done' WHERE queue_management_id = :id";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter("id", id);
    query.executeUpdate();
  }

  @Transactional
  public void updatePatientDelete(Integer id) {
    String sql1 = "UPDATE queue_management SET deleted = 1 WHERE queue_management_id = :id";
    Query query1 = entityManager.createNativeQuery(sql1);
    query1.setParameter("id", id);
    query1.executeUpdate();

    String sql2 = "UPDATE queue_management SET cancelled = 1 WHERE queue_management_id = :id";
    Query query2 = entityManager.createNativeQuery(sql2);
    query2.setParameter("id", id);
    query2.executeUpdate();
  }

  @Transactional
  public void SkipPatient(Integer id) {
    // Step 1: Retrieve the current slot ID for the given queue management ID
    String sql1 =
        "SELECT slot_id, clinic_id, doctor_id FROM queue_management WHERE queue_management_id = :id";
    Query query1 = entityManager.createNativeQuery(sql1);
    query1.setParameter("id", id);
    List<Object[]> result = query1.getResultList();

    if (!result.isEmpty()) {
      Object[] row = result.getFirst();
      Integer currentSlotId = (Integer) row[0];
      Integer clinicId = (Integer) row[1];
      String doctorId = (String) row[2];

      // Step 2: Find the next available slot from the slot_information table
      String sql2 =
          "SELECT slot_id FROM slot_information WHERE is_available = 1 AND slot_id > :currentSlotId AND clinic_id = :clinicId AND doctor_id = :doctorId ORDER BY slot_id ASC LIMIT 1";
      Query query2 = entityManager.createNativeQuery(sql2);
      query2.setParameter("currentSlotId", currentSlotId);
      query2.setParameter("clinicId", clinicId);
      query2.setParameter("doctorId", doctorId);
      List<Object> nextSlotIds = query2.getResultList();

      if (!nextSlotIds.isEmpty()) {
        Integer nextSlotId = (Integer) nextSlotIds.getFirst();

        // Step 3: Update the queue_management table with the new slot ID
        String sql3 =
            "UPDATE queue_management SET slot_id = :nextSlotId WHERE queue_management_id = :id";
        Query query3 = entityManager.createNativeQuery(sql3);
        query3.setParameter("nextSlotId", nextSlotId);
        query3.setParameter("id", id);
        query3.executeUpdate();

        // Step 4: Mark the new slot as unavailable and the previous slot as available
        String sql4 = "UPDATE slot_information SET is_available = 0 WHERE slot_id = :nextSlotId";
        Query query4 = entityManager.createNativeQuery(sql4);
        query4.setParameter("nextSlotId", nextSlotId);
        query4.executeUpdate();

        String sql5 = "UPDATE slot_information SET is_available = 1 WHERE slot_id = :currentSlotId";
        Query query5 = entityManager.createNativeQuery(sql5);
        query5.setParameter("currentSlotId", currentSlotId);
        query5.executeUpdate();

        log.info("Patient skipped to slot ID: {}", nextSlotId);
      } else {
        log.warn("No available slot found for skipping.");
      }
    } else {
      log.info("No slot ID found for queue_management_id: {}", id);
    }
  }
}
