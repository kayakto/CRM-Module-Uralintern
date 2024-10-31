package org.bitebuilders.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bitebuilders.controller.dto.EventDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Getter
@Table("events")
@NoArgsConstructor
public class Event {
    @Id
    private Long id;
    @Column("condition")
    @Setter
    private Condition condition; // status
    @Column("description_text")
    private String descriptionText;
    @Column("title")
    private String title;
    @Column("admin_id")
    private Long adminId;
    @Column("manager_id")
    private Long managerId;
    @Column("event_start_date")
    private OffsetDateTime eventStartDate;
    @Column("event_end_date")
    private OffsetDateTime eventEndDate;
    @Column("chat_url")
    private String chatUrl;
    @Column("enrollment_start_date")
    private OffsetDateTime enrollmentStartDate;
    @Column("enrollment_end_date")
    private OffsetDateTime enrollmentEndDate;
    @Column("number_seats")
    private int numberSeats;

    public Event(Condition condition, String descriptionText, String title, Long adminId, Long managerId, OffsetDateTime eventStartDate, OffsetDateTime eventEndDate, OffsetDateTime enrollmentStartDate, OffsetDateTime enrollmentEndDate, int numberSeats) {
        this.condition = condition;
        this.descriptionText = descriptionText;
        this.title = title;
        this.adminId = adminId;
        this.managerId = managerId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.enrollmentStartDate = enrollmentStartDate;
        this.enrollmentEndDate = enrollmentEndDate;
        this.numberSeats = numberSeats;
    }

    public Event(Long id, Condition condition, String descriptionText, String title, Long adminId, Long managerId, OffsetDateTime eventStartDate, OffsetDateTime eventEndDate, OffsetDateTime enrollmentStartDate, OffsetDateTime enrollmentEndDate, int numberSeats) {
        this.id = id;
        this.condition = condition;
        this.descriptionText = descriptionText;
        this.title = title;
        this.adminId = adminId;
        this.managerId = managerId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.enrollmentStartDate = enrollmentStartDate;
        this.enrollmentEndDate = enrollmentEndDate;
        this.numberSeats = numberSeats;
    }

    public EventDTO toEventDTO() {
        return new EventDTO(id, condition, descriptionText,
                title, adminId, managerId,
                eventStartDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                eventEndDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                chatUrl,
                enrollmentStartDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                enrollmentEndDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                numberSeats);
    }

    @Override
    public String toString() {
        return "Event{" +
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
        Event event = (Event) o;
        return numberSeats == event.numberSeats && Objects.equals(id, event.id) && condition == event.condition && Objects.equals(descriptionText, event.descriptionText) && Objects.equals(title, event.title) && Objects.equals(adminId, event.adminId) && Objects.equals(managerId, event.managerId) && Objects.equals(eventStartDate, event.eventStartDate) && Objects.equals(eventEndDate, event.eventEndDate) && Objects.equals(chatUrl, event.chatUrl) && Objects.equals(enrollmentStartDate, event.enrollmentStartDate) && Objects.equals(enrollmentEndDate, event.enrollmentEndDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, condition, descriptionText, title, adminId, managerId, eventStartDate, eventEndDate, chatUrl, enrollmentStartDate, enrollmentEndDate, numberSeats);
    }

    public enum Condition {
        HIDDEN,  // Скрыт
        ACTIVE,  // Активный
        DELETED, // Удаленный
        CLOSED   // Закрытый
    }
}
