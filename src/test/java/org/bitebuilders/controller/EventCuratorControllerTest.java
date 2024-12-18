package org.bitebuilders.controller;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.EventCuratorRepository;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EventCuratorControllerTest {

    @Autowired
    private EventCuratorController eventCuratorController;

    @Autowired
    private EventCuratorRepository eventCuratorRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    // Глобальные переменные для хранения ID
    private Long curatorId;
    private Long eventId;
    private Long curatorIdWithoutEvent;

    @BeforeEach
    void setUp() {
        // Очистка базы перед каждым тестом
        eventCuratorRepository.deleteAll();
        eventRepository.deleteAll();
        userInfoRepository.deleteAll();

        // Добавление тестовых данных
        UserInfo curator1 = new UserInfo(
                "John",
                "Doe",
                null,
                "johndoe@example.com",
                "John's sign",
                "vk.com/johndoe",
                "t.me/johndoe",
                UserInfo.Role.CURATOR,
                "Chill guy"

        );
        curatorId = userInfoRepository.save(curator1).getId();

        UserInfo curatorWithoutEvent = new UserInfo(
                "Andrew",
                "Doe",
                null,
                "doe@example.com",
                "Andrew's sign",
                "vk.com/andrewdoe",
                "t.me/andrewdoe",
                UserInfo.Role.CURATOR,
                "Chill guy"

        );
        curatorIdWithoutEvent = userInfoRepository.save(curatorWithoutEvent).getId();

        Event event1 = new Event(
                Event.Condition.REGISTRATION_OPEN,
                "description",
                "title",
                null,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                100
        );
        eventId = eventRepository.save(event1).getId();

        EventCurator eventCurator = new EventCurator(
                curatorId,
                eventId,
                StatusRequest.SENT_PERSONAL_INFO
        );
        eventCuratorRepository.save(eventCurator);
    }
    // TODO мб сделать скрипт для создания этих друзей, а не в коде их объявлять

    @Test
    void testGetCuratorsInfo() {
        var response = eventCuratorController.getCuratorsInfo(eventId);

        assertNotNull(response);
        assertTrue(response.getBody() != null && !response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        assertEquals(
                StatusRequest.SENT_PERSONAL_INFO,
                response.getBody().get(0).getCuratorStatus());
    }

    @Test
    void testSendCuratorRequest() {
        var response = eventCuratorController.sendCuratorToEvent(eventId, curatorIdWithoutEvent);

        assertNotNull(response);
        assertTrue(response.getBody());
        assertEquals(
                StatusRequest.SENT_PERSONAL_INFO,
                eventCuratorRepository.findCuratorEvent(curatorIdWithoutEvent, eventId).get().getCuratorStatus());
    }

    @Test
    void testGetSentCuratorsInfo() {
        // добавим второго куратора на мероприятие
        eventCuratorController.sendCuratorToEvent(eventId, curatorIdWithoutEvent);
        var response = eventCuratorController.getSentCuratorsInfo(eventId);

        assertNotNull(response);
        assertTrue(response.getBody() != null && !response.getBody().isEmpty());
        assertEquals(2, response.getBody().size());
        assertEquals(
                StatusRequest.SENT_PERSONAL_INFO,
                response.getBody().get(0).getCuratorStatus());
    }

    @Test
    void testDeleteCuratorFromEvent() {
        var response = eventCuratorController.deleteCuratorFromEvent(eventId, curatorId);

        assertNotNull(response);
        assertTrue(response.getBody());
        assertEquals(
                StatusRequest.DELETED_FROM_EVENT,
                eventCuratorRepository.findCuratorEvent(curatorId, eventId).get().getCuratorStatus());
    }

    @Test
    void testAcceptCuratorRequest() {
        var response = eventCuratorController.acceptCuratorRequest(eventId, curatorId);

        assertNotNull(response);
        assertTrue(response.getBody());
        assertEquals(
                StatusRequest.ADDED_IN_CHAT,
                eventCuratorRepository.findCuratorEvent(curatorId, eventId).get().getCuratorStatus());
    }

    @Test
    void testRejectCuratorRequest() {
        var response = eventCuratorController.rejectCuratorRequest(eventId, curatorId);

        assertNotNull(response);
        assertTrue(response.getBody());
        assertEquals(
                StatusRequest.REJECTED_FROM_EVENT,
                eventCuratorRepository.findCuratorEvent(curatorId, eventId).get().getCuratorStatus());
    }
}

