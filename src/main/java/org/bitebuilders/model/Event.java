package org.bitebuilders.model;

import lombok.*;
import org.bitebuilders.controller.dto.EventDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Table("events")
@NoArgsConstructor
@Setter
@ToString
@EqualsAndHashCode
public class Event {
    @Id
    private Long id;
    @Column("condition")
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
    @Column("number_seats_students")
    private int numberSeatsStudent;

    public enum Condition {
        PREPARATION, // Статус "Подготовка" : (текущая дата < enrollment_start_date)
        REGISTRATION_OPEN, // Статус "Регистрация открыта" : (enrollment_start_date <= текущая дата <= enrollment_end_date, number_seats > 0).
        NO_SEATS, // Статус "Мест нет" : (enrollment_start_date <= текущая дата <= enrollment_end_date, number_seats=0).
        REGISTRATION_CLOSED, // Статус "Регистрация закрыта" : (enrollment_end_date < текущая дата < event_start_date)
        IN_PROGRESS, // Статус "В процессе проведения" : (event_start_date <= текущая дата <= event_end_date)
        FINISHED, // Статус "Завершено" : (текущая дата > event_end_date)
        HIDDEN, // Статус "Скрыто" : Мероприятие вручную скрыто администратором
        DELETED // Статус "Удалено" : Мероприятие вручную удалено администратором
    }

    public void setConditionToStarted() {
        this.enrollmentEndDate = OffsetDateTime.now();
        this.eventStartDate = OffsetDateTime.now();
        this.condition = Condition.IN_PROGRESS;
    }

    public Event(String descriptionText, String title, Long adminId, Long managerId, OffsetDateTime eventStartDate, OffsetDateTime eventEndDate, OffsetDateTime enrollmentStartDate, OffsetDateTime enrollmentEndDate, String chatUrl, int numberSeatsStudent) {
        this.descriptionText = descriptionText;
        this.title = title;
        this.adminId = adminId;
        this.managerId = managerId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.enrollmentStartDate = enrollmentStartDate;
        this.enrollmentEndDate = enrollmentEndDate;
        this.chatUrl = chatUrl;
        this.numberSeatsStudent = numberSeatsStudent;
    }

    public Event(Long id, String descriptionText, String title, Long adminId, Long managerId, OffsetDateTime eventStartDate, OffsetDateTime eventEndDate, OffsetDateTime enrollmentStartDate, OffsetDateTime enrollmentEndDate, String chatUrl, int numberSeatsStudent) {
        this.id = id;
        this.descriptionText = descriptionText;
        this.title = title;
        this.adminId = adminId;
        this.managerId = managerId;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.enrollmentStartDate = enrollmentStartDate;
        this.enrollmentEndDate = enrollmentEndDate;
        this.chatUrl = chatUrl;
        this.numberSeatsStudent = numberSeatsStudent;
    }

    public EventDTO toEventDTO() {
        return new EventDTO(id, condition, descriptionText,
                title, adminId, managerId,
                eventStartDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                eventEndDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                chatUrl,
                enrollmentStartDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                enrollmentEndDate.withOffsetSameInstant(ZoneOffset.ofHours(5)),
                numberSeatsStudent);
    }
}
