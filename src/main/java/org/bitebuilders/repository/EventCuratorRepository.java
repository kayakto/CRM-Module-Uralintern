package org.bitebuilders.repository;

import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventCuratorInfo;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCuratorRepository extends CrudRepository<EventCurator, Long> {

    @Query("SELECT ec.event_id, ec.curator_id, " +
            "ui.first_name, ui.last_name, ui.surname, ui.telegram_url, ui.vk_url " +
            "FROM events_curators ec " +
            "JOIN users_info ui ON ec.curator_id = ui.id " +
            "WHERE ec.event_id = :eventId")
    List<EventCuratorInfo> findByEventId(Long eventId);
}
