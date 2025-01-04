package org.bitebuilders.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bitebuilders.model.Event;

import java.time.OffsetDateTime;

@Data
@Getter
@AllArgsConstructor
public class EventRequest {
    private final String title;
    private final String descriptionText;
    private final Long adminId;
    private final Long managerId;
    private final OffsetDateTime eventStartDate;
    private final OffsetDateTime eventEndDate;
    private final OffsetDateTime enrollmentStartDate;
    private final OffsetDateTime enrollmentEndDate;
    private final String chatUrl;
    private final int numberSeatsStudent;
    private final Event.Condition condition; // TODO не должно передаваться

    public Event toEvent() {
        return new Event(condition != null ? condition : Event.Condition.REGISTRATION_OPEN,
                descriptionText, title,
                adminId, managerId, eventStartDate, eventEndDate,
                enrollmentStartDate, enrollmentEndDate, chatUrl, numberSeatsStudent);
    }

    public Event toEvent(Long eventId) {
        return new Event(eventId,
                condition != null ? condition : Event.Condition.REGISTRATION_OPEN,
                descriptionText, title,
                adminId, managerId, eventStartDate, eventEndDate,
                enrollmentStartDate, enrollmentEndDate, chatUrl, numberSeatsStudent);
    }
}
