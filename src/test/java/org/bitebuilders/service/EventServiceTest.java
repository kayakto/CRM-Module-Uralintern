package org.bitebuilders.service; // для теста старта отдельного event в EventServiceStartTest

import org.bitebuilders.model.Event;
import org.bitebuilders.model.UserInfo;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EventServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private EventService eventService;

    private Long adminId;

    private Long eventId;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        eventRepository.deleteAll();
        userInfoRepository.deleteAll();

        // Создаем админа
        UserInfo admin = new UserInfo(
                "John",
                "Doe",
                null,
                "johndoe@example.com",
                "John's sign",
                "vk.com/johndoe",
                "t.me/johndoe",
                UserInfo.Role.ADMIN,
                "Chill guy"
        );

        // Сохраняем админа в базе данных
        adminId = userInfoRepository.save(admin).getId();

        // Создаем запись события
        Event event1 = new Event(
                Event.Condition.REGISTRATION_OPEN,
                "description",
                "title",
                adminId, // Используем сохраненный adminId
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                100
        );

        // Сохраняем событие в базе данных
        eventId = eventRepository.save(event1).getId();
    }

    @Test
    void getOpenedEvents_ShouldReturnOpenedOpenedOpenedEvents() {
        // Act
        List<Event> openedEvents = eventService.getOpenedEvents();

        // Assert
        assertNotNull(openedEvents);
        assertEquals(1, openedEvents.size());
        assertEquals(Event.Condition.REGISTRATION_OPEN, openedEvents.get(0).getCondition());
    }

    @Test
    void getEventsByAdminId_ShouldReturnEventsForAdmin() {
        // Act
        List<Event> events = eventService.getEventsByAdminId(adminId);

        // Assert
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(adminId, events.get(0).getAdminId());
    }

    @Test
    void getEventById_ShouldReturnEventWhenExists() {
        // Act
        Optional<Event> result = eventService.getEventById(eventId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(eventId, result.get().getId());
    }

    @Test
    void createOrUpdateEvent_ShouldSaveAndReturnEvent() {
        // Arrange
        Event event = new Event(
                Event.Condition.REGISTRATION_OPEN,
                "description",
                "title",
                adminId,
                null,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null,
                100
        );

        // Act
        Event savedEvent = eventService.createOrUpdateEvent(event);

        // Assert
        assertNotNull(savedEvent);
        assertEquals("title", savedEvent.getTitle());
    }

    @Test
    void deleteEvent_ShouldSetConditionToDeletedWhenEventExists() {
        // Act
        Boolean result = eventService.deleteEvent(eventId);

        // Assert
        assertTrue(result);
        Event updatedEvent = eventRepository.findById(eventId).orElseThrow();
        assertEquals(Event.Condition.DELETED, updatedEvent.getCondition());
    }

    @Test
    void deleteEvent_ShouldReturnFalseWhenEventDoesNotExist() {
        // Act
        Boolean result = eventService.deleteEvent(999L);

        // Assert
        assertFalse(result);
    }
}

