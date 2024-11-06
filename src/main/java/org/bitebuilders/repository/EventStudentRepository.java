package org.bitebuilders.repository;

import org.bitebuilders.StudentStatus;
import org.bitebuilders.model.EventStudent;
import org.bitebuilders.model.EventStudentInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStudentRepository extends CrudRepository<EventStudent, Long> {
    // Метод для поиска студентов по статусу
    @Query("SELECT * FROM events_students WHERE student_status = :studentStatus::student_status")
    List<EventStudent> findByStudentStatus(StudentStatus studentStatus);

    @Query("SELECT es.event_id, es.student_id, es.student_status, " +
            "ui.first_name, ui.last_name, ui.surname, ui.competencies, ui.telegram_url, ui.vk_url " +
            "FROM events_students es " +
            "JOIN users_info ui ON es.student_id = ui.id " +
            "WHERE es.event_id = :eventId")
    List<EventStudentInfo> findByEventId(Long eventId);
}
