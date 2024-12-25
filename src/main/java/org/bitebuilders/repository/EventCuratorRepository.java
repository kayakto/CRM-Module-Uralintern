package org.bitebuilders.repository;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventCuratorInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventCuratorRepository extends CrudRepository<EventCurator, Long> {

    @Query("SELECT ec.event_id, ec.curator_id, ec.curator_status, ui.competencies, " +
            "ui.first_name, ui.last_name, ui.surname, ui.telegram_url, ui.vk_url " +
            "FROM events_curators ec " +
            "JOIN users_info ui ON ec.curator_id = ui.id " +
            "WHERE ec.event_id = :eventId AND curator_status <> 'DELETED_FROM_EVENT'")
    List<EventCuratorInfo> findByEventId(Long eventId);

    @Query("SELECT ec.event_id, ec.curator_id, ec.curator_status, ui.competencies, " +
            "ui.first_name, ui.last_name, ui.surname, ui.telegram_url, ui.vk_url " +
            "FROM events_curators ec " +
            "JOIN users_info ui ON ec.curator_id = ui.id " +
            "WHERE ec.event_id = :eventId AND ec.curator_status = 'SENT_PERSONAL_INFO'")
    List<EventCuratorInfo> findWaitingCuratorsInfo(Long eventId);

    @Query("SELECT ec.event_id, ec.curator_id, ec.curator_status, ui.competencies, " +
            "ui.first_name, ui.last_name, ui.surname, ui.telegram_url, ui.vk_url " +
            "FROM events_curators ec " +
            "JOIN users_info ui ON ec.curator_id = ui.id " +
            "WHERE ec.event_id = :eventId AND ec.curator_status = 'ADDED_IN_CHAT'")
    List<EventCuratorInfo> findAcceptedCuratorsInfo(Long eventId);

    // Метод для получения кураторов со статусом "ADDED_IN_CHAT" для определённого события
    @Query("SELECT ec.id, ec.curator_id, ec.event_id, ec.curator_status " +
            "FROM events_curators ec " +
            "WHERE ec.event_id = :eventId AND ec.curator_status = 'ADDED_IN_CHAT'")
    List<EventCurator> findAcceptedEventCurator(Long eventId);

    // Метод для поиска куратора по ID куратора и ID события
    @Query("SELECT ec.id, ec.curator_id, ec.event_id, ec.curator_status " +
            "FROM events_curators ec " +
            "WHERE ec.curator_id = :curatorId AND ec.event_id = :eventId")
    Optional<EventCurator> findCuratorEvent(Long curatorId, Long eventId);

    @Query("SELECT curator_status FROM events_curators WHERE curator_id = :curatorId AND event_id = :eventId")
    StatusRequest findCuratorEventStatus(Long curatorId, Long eventId);

    @Query("SELECT e.* FROM events e " +
            "JOIN events_curators ec ON ec.event_id = e.id " +
            "WHERE ec.curator_id = :curatorId AND ec.curator_status = 'ADDED_IN_CHAT'")
    List<Event> findAcceptedEventsByCurator(Long curatorId);
}
