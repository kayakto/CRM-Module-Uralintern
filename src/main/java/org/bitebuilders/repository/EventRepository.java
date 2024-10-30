package org.bitebuilders.repository;

import org.bitebuilders.model.Event;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    // Метод для поиска по статусу
    @Query("SELECT * FROM events WHERE condition = CAST(:condition AS event_condition);")
    List<Event> findAllByCondition(Event.Condition condition);

    // Метод для поиска по adminId
    @Query("SELECT * FROM events WHERE admin_id = :adminId;")
    List<Event> findAllByAdminId(@Param("adminId") Long adminId);

    @Modifying
    @Transactional
    @Query("INSERT INTO events (condition, description_text, title, admin_id, manager_id, event_start_date, event_end_date, chat_url, enrollment_start_date, enrollment_end_date, number_seats) " +
            "VALUES (CAST(:#{#event.condition.name()} AS event_condition), :#{#event.descriptionText}, :#{#event.title}, :#{#event.adminId}, :#{#event.managerId}, :#{#event.eventStartDate}, :#{#event.eventEndDate}, :#{#event.chatUrl}, :#{#event.enrollmentStartDate}, :#{#event.enrollmentEndDate}, :#{#event.numberSeats}) " +
            "RETURNING id")
    Event saveAndReturnEvent(Event event);

    // Метод для удаления из базы данных удаленных пользователем записей,
    // которые были удалены им более 30 дней назад
    @Transactional
    @Modifying
    @Query("DELETE FROM Event e WHERE e.condition = CAST(:condition AS event_condition) AND e.updatedAt < :dateTime")
    void deleteByConditionAndUpdatedBefore(Event.Condition condition, LocalDateTime dateTime);
}
