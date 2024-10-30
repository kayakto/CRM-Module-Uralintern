package org.bitebuilders.repository;

import org.bitebuilders.model.EventStudent;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventStudentRepository extends CrudRepository<EventStudent, Long> {
    // Метод для поиска студентов по статусу
    @Query("SELECT * FROM events_students WHERE student_status = :studentStatus::student_status")
    List<EventStudent> findByStudentStatus(EventStudent.StudentStatus studentStatus);
    @Query("SELECT * FROM events_students WHERE event_id = :eventId")
    List<EventStudent> findByEventId(Long eventId); // TODO тут вся инфа дб по студенту
}
