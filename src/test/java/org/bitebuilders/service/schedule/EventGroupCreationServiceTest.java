package org.bitebuilders.service.schedule;

import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.*;
import org.bitebuilders.service.EventGroupService;
import org.bitebuilders.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@Transactional
class EventGroupCreationServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventGroupService eventGroupService;

    @Autowired
    private EventGroupCreationService eventGroupCreationService;

    private final int expectedNumberOfGroups = 1;

    @BeforeEach
    void executeSqlFromFile() throws IOException {
        Resource resource = new ClassPathResource("test-data.sql");
        String sql = new String(Files.readAllBytes(resource.getFile().toPath()));
        jdbcTemplate.execute(sql);
    }

    @Test
    void testCheckAndCreateEventGroups() {
        OffsetDateTime currentTime = OffsetDateTime.now();
        Event testEvent = eventService.getAllEvents().get(0);
        Long eventId = testEvent.getId();
        testEvent.setEnrollmentStartDate(currentTime.minusMinutes(10));
        eventService.createOrUpdateEvent(testEvent);

        assertDoesNotThrow(() -> eventGroupCreationService.startEvents());

        Event updatedEvent = eventService.getEventById(eventId).orElseThrow();
        List<EventGroup> eventGroups = eventGroupService.getEventGroups(eventId);

        assertAll(
                () -> assertEquals(Event.Condition.IN_PROGRESS, updatedEvent.getCondition(), "Event status should be IN PROGRESS"),
                () -> assertFalse(eventGroups.isEmpty(), "Groups should be created for the event"),
                () -> assertEquals(expectedNumberOfGroups, eventGroups.size(), "Number of groups should match the expected count")
        );
    }

    @Test
    void testCheckAndCreateEventGroupsNoEvents() {
        eventService.deleteAllEvents();

        assertDoesNotThrow(() -> eventGroupCreationService.startEvents());

        List<Event> events = eventService.getAllEvents();
        assertTrue(events.isEmpty(), "No events should be present");
    }

    @Test
    void testStartEventSuccess() {
        // Получение тестового события
        Event testEvent = eventService.getAllEvents().get(0);
        Long eventId = testEvent.getId();

        // Убедимся, что статус события позволяет начать
        testEvent.setCondition(Event.Condition.REGISTRATION_OPEN);
        eventService.createOrUpdateEvent(testEvent);

        // Запускаем событие
        Event startedEvent = eventGroupCreationService.startEventById(eventId);

        // Проверяем, что группы созданы и статус изменился
        List<EventGroup> eventGroups = eventGroupService.getEventGroups(eventId);

        assertAll(
                () -> assertEquals(Event.Condition.IN_PROGRESS, startedEvent.getCondition(), "Event status should be IN_PROGRESS"),
                () -> assertFalse(eventGroups.isEmpty(), "Event groups should be created"),
                () -> assertTrue(eventGroups.size() > 0, "There should be at least one group created")
        );
    }

    @Test
    void testStartEventNotFound() {
        // Передаем несуществующий ID события
        Long nonExistentEventId = 999L;

        // Проверяем, что выбрасывается исключение
        assertThrows(EventNotFoundException.class, () -> eventGroupCreationService.startEventById(nonExistentEventId));
    }

    @Test
    void testStartEventInvalidCondition() {
        // Получаем тестовое событие с неподходящим состоянием
        Event testEvent = eventService.getAllEvents().get(0);
        Long eventId = testEvent.getId();

        // Устанавливаем неподходящее состояние
        testEvent.setCondition(Event.Condition.HIDDEN);
        eventService.createOrUpdateEvent(testEvent);

        // Проверяем, что метод выбрасывает исключение
        assertThrows(IllegalStateException.class, () -> eventGroupCreationService.startEventById(eventId));

        // Убедимся, что статус события не изменился
        Event unchangedEvent = eventService.getEventById(eventId).orElseThrow();
        assertEquals(Event.Condition.HIDDEN, unchangedEvent.getCondition(), "Event status should remain HIDDEN");
    }

    @Test
    void testStartEventNoGroupsCreated() {
        // Получение тестового события
        Event testEvent = eventService.getAllEvents().get(0);
        Long eventId = testEvent.getId();

        // Устанавливаем состояние, чтобы группы не создавались (например, пустая логика)
        testEvent.setCondition(Event.Condition.REGISTRATION_OPEN);
        eventService.createOrUpdateEvent(testEvent);

        // Удаляем всех студентов, чтобы группы не могли быть созданы
        jdbcTemplate.execute("DELETE FROM events_students WHERE event_id = " + eventId);

        assertThrows(EventUserNotFoundException.class, () -> eventGroupCreationService.startEventById(eventId), "No students available for this event.");
    }
}


