package org.bitebuilders.service;

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
}
