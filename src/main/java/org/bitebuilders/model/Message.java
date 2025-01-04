package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.bitebuilders.enums.StatusRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Table("messages")
public class Message {
    @Id
    private Long id;
    @Column("event_id")
    private Long eventId;
    @Setter
    @Column("text")
    private String text;
    @Column("status")
    private MessageStatus messageStatus;
    @Setter
    @Column("edit_date")
    private OffsetDateTime editDate; // добавим таблицу сообщения когда было отправлено добавить

    // Конструктор с параметром по умолчанию
    public Message(Long eventId, String text, MessageStatus messageStatus) {
        this(messageStatus, text, eventId, OffsetDateTime.now());
    }

    // Основной конструктор
    public Message(MessageStatus messageStatus, String text, Long eventId, OffsetDateTime editDate) {
        this.messageStatus = messageStatus;
        this.text = text;
        this.eventId = eventId;
        this.editDate = editDate;
    }
    // статус сообщения на accept reject

    public enum MessageStatus {
        ACCEPTED,
        DECLINED
    }

    public static MessageStatus parseStatusRequestToMessageStatus(StatusRequest statusRequest) {
        return switch (statusRequest) {
            case ADDED_IN_CHAT -> MessageStatus.ACCEPTED;
            case REJECTED_FROM_EVENT -> MessageStatus.DECLINED;
            default -> throw new IllegalArgumentException("Unsupported StatusRequest: " + statusRequest);
        };
    }
}
