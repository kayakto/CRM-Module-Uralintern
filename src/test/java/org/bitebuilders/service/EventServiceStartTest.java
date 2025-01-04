package org.bitebuilders.service;

import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.EventGroup;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest()
@Transactional
public class EventServiceStartTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventGroupService eventGroupService;

    @BeforeEach
    void executeSqlFromFile() throws IOException {
        Resource resource = new ClassPathResource("test-data.sql");
        String sql = new String(Files.readAllBytes(resource.getFile().toPath()));
        jdbcTemplate.execute(sql);
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
        Event startedEvent = eventService.startEventById(eventId);

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
        assertThrows(EventNotFoundException.class, () -> eventService.startEventById(nonExistentEventId));
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
        assertThrows(IllegalStateException.class, () -> eventService.startEventById(eventId));

        // Убедимся, что статус события не изменился
        Event unchangedEvent = eventService.getEventById(eventId);
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

        assertThrows(EventUserNotFoundException.class, () -> eventService.startEventById(eventId), "No students available for this event.");
    }
}
