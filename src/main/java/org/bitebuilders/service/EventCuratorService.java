package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventCuratorInfo;
import org.bitebuilders.repository.EventCuratorRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventCuratorService {

    @Autowired
    private final EventCuratorRepository eventCuratorRepository;

    @Autowired
    private final UserInfoRepository userInfoRepository;

    public EventCuratorService(EventCuratorRepository eventCuratorRepository, UserInfoRepository userInfoRepository) {
        this.eventCuratorRepository = eventCuratorRepository;
        this.userInfoRepository = userInfoRepository;
    }

    public EventCurator getEventCurator(Long eventId, Long curatorId) {
        Optional<EventCurator> eventCurator = eventCuratorRepository.findCuratorEvent(curatorId, eventId);
        return eventCurator.orElse(null);
    }

    // Метод возвращающий список всех заявок на кураторство.
    public List<EventCuratorInfo> getSentCuratorInfo(Long eventId) {
        return eventCuratorRepository.findWaitingCuratorsInfo(eventId);
    }

    public List<EventCurator> getAcceptedEventCurator(Long eventId) {
        return eventCuratorRepository.findAcceptedEventCurator(eventId);
    }

    public List<EventCuratorInfo> getCuratorsInfo(Long eventId) {
        return eventCuratorRepository.findByEventId(eventId);
    }

    public StatusRequest getCuratorStatus(Long eventId, Long curatorId) {
        return eventCuratorRepository.findCuratorEventStatus(curatorId, eventId); // todo throw exception
    }

    public EventCurator save(EventCurator curator) {
        return eventCuratorRepository.save(curator);
    }

    /**
     * Метод, который меняет статус куратора
     */
    @Transactional
    public Boolean updateCuratorStatus(Long eventId, Long curatorId, StatusRequest newStatus) {
        Optional<EventCurator> optionalEventCurator = eventCuratorRepository.findCuratorEvent(curatorId, eventId);

        EventCurator eventCurator;
        if (optionalEventCurator.isPresent()) {
            eventCurator = optionalEventCurator.get();
        } else {
            if (newStatus == StatusRequest.SENT_PERSONAL_INFO &&
                    userInfoRepository.findById(curatorId).isPresent()) { // тут добавить проверку на существование куратора
                eventCurator = new EventCurator(curatorId, eventId, newStatus);
            } else {
                throw new EventUserNotFoundException(
                        "EventCurator not found for eventId: " + eventId + " and curatorId: " + curatorId
                ); // TODO переделать так, чтобы были методы на проверку существования
            }
        }

        eventCurator.setCuratorStatus(newStatus);
        EventCurator savedEventCurator = eventCuratorRepository.save(eventCurator);

        return savedEventCurator.getCuratorStatus() == newStatus;
    }
}
