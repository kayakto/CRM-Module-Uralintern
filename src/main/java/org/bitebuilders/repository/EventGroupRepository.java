package org.bitebuilders.repository;

import org.bitebuilders.model.EventGroup;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventGroupRepository extends CrudRepository<EventGroup, Long> {

    @Query("SELECT * FROM event_groups " +
            "WHERE event_id = :eventId AND curator_id = :curatorId")
    EventGroup findByEventIdAndCuratorId(Long eventId, Long curatorId);

    @Query("SELECT * FROM event_groups " +
            "WHERE event_id = :eventId")
    List<EventGroup> findByEventId(Long eventId);
}
