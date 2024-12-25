package org.bitebuilders.repository;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventStudent;
import org.bitebuilders.model.EventStudentInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventStudentRepository extends CrudRepository<EventStudent, Long> {
    // Метод для поиска студентов по статусу
    @Query("SELECT * FROM events_students WHERE student_status = :studentStatus::student_status")
    List<EventStudent> findByStudentStatus(StatusRequest statusRequest);

    @Query("SELECT es.event_id, es.student_id, es.student_status, " +
            "uis.first_name, uis.last_name, uis.surname, uis.competencies, uis.telegram_url, uis.vk_url, " +
            "ec.curator_id, uic.first_name AS curator_first_name, uic.last_name AS curator_last_name, uic.surname AS curator_surname, " +
            "eg.id AS group_id " +
            "FROM events_students es " +
            "JOIN users_info uis ON es.student_id = uis.id " +
            "LEFT JOIN event_groups eg ON es.group_id = eg.id " +
            "LEFT JOIN events_curators ec ON ec.curator_id = eg.curator_id AND ec.event_id = eg.event_id "+
            "LEFT JOIN users_info uic ON ec.curator_id = uic.id " +
            "WHERE es.event_id = :eventId AND es.student_status <> 'DELETED_FROM_EVENT'")
    List<EventStudentInfo> findByEventId(Long eventId);

    // тут нет запросов кураторов, потому что их еще не присвоили
    @Query("SELECT es.event_id, es.student_id, es.student_status, ui.competencies, " +
            "ui.first_name, ui.last_name, ui.surname, ui.telegram_url, ui.vk_url " +
            "FROM events_students es " +
            "JOIN users_info ui ON es.student_id = ui.id " +
            "WHERE es.event_id = :eventId AND es.student_status = 'SENT_PERSONAL_INFO'")
    List<EventStudentInfo> findWaitingStudentsInfo(Long eventId);

    @Query("SELECT es.event_id, es.student_id, es.student_status, ui.competencies, " +
            "ui.first_name, ui.last_name, ui.surname, ui.telegram_url, ui.vk_url " +
            "FROM events_students es " +
            "JOIN users_info ui ON es.student_id = ui.id " +
            "WHERE es.event_id = :eventId AND es.student_status = 'ADDED_IN_CHAT'")
    List<EventStudentInfo> findAcceptedStudentsInfo(Long eventId);

    @Query("SELECT es.event_id, es.student_id, es.student_status " +
            "FROM events_students es " +
            "WHERE es.event_id = :eventId AND es.student_status = 'ADDED_IN_CHAT'")
    List<EventStudent> findAcceptedEventStudent(Long eventId); // TODO check in chat

    @Query("SELECT * FROM events_students WHERE student_id = :studentId AND event_id = :eventId")
    Optional<EventStudent> findStudentEvent(Long studentId, Long eventId);

    @Query("SELECT student_status FROM events_students WHERE student_id = :studentId AND event_id = :eventId")
    StatusRequest findStudentEventStatus(Long studentId, Long eventId);

    @Query("SELECT e.* FROM events e " +
            "JOIN events_students es ON es.event_id = e.id " +
            "WHERE es.student_id = :studentId AND es.student_status = 'ADDED_IN_CHAT'")
    List<Event> findAcceptedEventsByStudent(Long studentId);
}
