package org.bitebuilders.repository;

import org.bitebuilders.model.Event;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
    // Метод для поиска по статусу
    @Query("SELECT * FROM events WHERE condition = :condition;")
    List<Event> findAllByCondition(Event.Condition condition); // тут конвертер работает

    // Метод для поиска по adminId
    @Query("SELECT * FROM events WHERE admin_id = :adminId AND condition != 'DELETED';")
    List<Event> findAllByAdminId(Long adminId); // 4

    // Метод для удаления из базы данных удаленных пользователем записей,
    // которые были удалены им более 30 дней назад
    @Transactional
    @Modifying
    @Query("DELETE FROM Event e WHERE e.condition = :condition AND e.updatedAt < :dateTime")
    void deleteByConditionAndUpdatedBefore(Event.Condition condition, LocalDateTime dateTime);
}
