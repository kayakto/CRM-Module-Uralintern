package org.bitebuilders.service;

import org.bitebuilders.model.Event;
import org.bitebuilders.repository.EventRepository;
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
    private EventService eventService;

    @BeforeEach
    void setUp() {
        // Очистка базы данных перед каждым тестом
        eventRepository.deleteAll();
        // Создаем несколько тестовых записей
        Event event1 = new Event(
                Event.Condition.ACTIVE,
                "description",
                "title",
                1L,
                2L,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                100
        );

        eventRepository.save(event1);
    }

    @Test
    void getActiveEvents_ShouldReturnActiveEvents() {
        // Act
        List<Event> activeEvents = eventService.getActiveEvents();

        // Assert
        assertNotNull(activeEvents);
        assertEquals(1, activeEvents.size());
        assertEquals(Event.Condition.ACTIVE, activeEvents.get(0).getCondition());
    }

    @Test
    void getEventsByAdminId_ShouldReturnEventsForAdmin() {
        // Arrange
        Long adminId = 1L;

        // Act
        List<Event> events = eventService.getEventsByAdminId(adminId);

        // Assert
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(adminId, events.get(0).getAdminId());
    }

    @Test
    void getEventById_ShouldReturnEventWhenExists() {
        // Arrange
        Long eventId = eventRepository.findAllByCondition(Event.Condition.ACTIVE).get(0).getId();

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
                Event.Condition.ACTIVE,
                "description",
                "title",
                1L,
                2L,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
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
        // Arrange
        Long eventId = eventRepository.findAllByCondition(Event.Condition.ACTIVE).get(0).getId();

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

