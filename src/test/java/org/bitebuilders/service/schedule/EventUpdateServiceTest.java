package org.bitebuilders.service.schedule;

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
class EventUpdateServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventGroupService eventGroupService;

    @Autowired
    private EventUpdateService eventUpdateService;

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
        testEvent.setEventStartDate(currentTime.minusMinutes(10));
        eventService.createOrUpdateEvent(testEvent);

        assertDoesNotThrow(() -> eventUpdateService.startEvents());

        Event updatedEvent = eventService.getEventById(eventId);
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

        assertDoesNotThrow(() -> eventUpdateService.startEvents());

        List<Event> events = eventService.getAllEvents();
        assertTrue(events.isEmpty(), "No events should be present");
    }
}


