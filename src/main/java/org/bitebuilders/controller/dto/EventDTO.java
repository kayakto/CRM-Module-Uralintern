package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bitebuilders.model.Event;

import java.time.OffsetDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
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
    private final int numberSeats;

    @Override
    public String toString() {
        return "EventDTO{" +
                "id=" + id +
                ", condition=" + condition +
                ", descriptionText='" + descriptionText + '\'' +
                ", title='" + title + '\'' +
                ", adminId=" + adminId +
                ", managerId=" + managerId +
                ", eventStartDate=" + eventStartDate +
                ", eventEndDate=" + eventEndDate +
                ", chatUrl='" + chatUrl + '\'' +
                ", enrollmentStartDate=" + enrollmentStartDate +
                ", enrollmentEndDate=" + enrollmentEndDate +
                ", numberSeats=" + numberSeats +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDTO eventDTO = (EventDTO) o;
        return numberSeats == eventDTO.numberSeats && Objects.equals(id, eventDTO.id) && condition == eventDTO.condition && Objects.equals(descriptionText, eventDTO.descriptionText) && Objects.equals(title, eventDTO.title) && Objects.equals(adminId, eventDTO.adminId) && Objects.equals(managerId, eventDTO.managerId) && Objects.equals(eventStartDate, eventDTO.eventStartDate) && Objects.equals(eventEndDate, eventDTO.eventEndDate) && Objects.equals(chatUrl, eventDTO.chatUrl) && Objects.equals(enrollmentStartDate, eventDTO.enrollmentStartDate) && Objects.equals(enrollmentEndDate, eventDTO.enrollmentEndDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, condition, descriptionText, title, adminId, managerId, eventStartDate, eventEndDate, chatUrl, enrollmentStartDate, enrollmentEndDate, numberSeats);
    }
}
