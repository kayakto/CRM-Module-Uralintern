package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bitebuilders.model.Event;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class EventDTO {
    private final Long id;
    private final Event.Condition condition; // status
    private final String descriptionText;
    private final String title;
    private final Long adminId;
    private final Long managerId;
    private final OffsetDateTime eventStartDate;
    private final OffsetDateTime eventEndDate;
    private final String chatUrl;
    private final OffsetDateTime enrollmentStartDate;
    private final OffsetDateTime enrollmentEndDate;
    private final int numberSeatsStudent;
}
