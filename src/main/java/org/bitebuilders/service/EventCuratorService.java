package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventCuratorInfo;
import org.bitebuilders.repository.EventCuratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCuratorService {

    @Autowired
    private final EventCuratorRepository eventCuratorRepository;

    public EventCuratorService(EventCuratorRepository eventCuratorRepository) {
        this.eventCuratorRepository = eventCuratorRepository;
    }

    public List<EventCuratorInfo> getCuratorInfo(Long eventId) {
        return eventCuratorRepository.findByEventId(eventId);
    }

    // Метод, который меняет статус студента
    public Boolean updateCuratorStatus(Long eventId, Long curatorId, StatusRequest newStatus) {
        EventCurator eventCurator = eventCuratorRepository.findCuratorEvent(curatorId, eventId);
        eventCurator.setCuratorStatus(newStatus);
        EventCurator savedEventCurator = eventCuratorRepository.save(eventCurator);
        return savedEventCurator.getCuratorStatus() == newStatus;
    }
}
