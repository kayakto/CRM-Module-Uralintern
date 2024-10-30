package org.bitebuilders.repository;

import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventCuratorRepository extends CrudRepository<EventCurator, Long> {
}
