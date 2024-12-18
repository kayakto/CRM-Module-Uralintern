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
    @Column("edit_date")
    private OffsetDateTime editDate; // добавим таблицу сообщения когда было отправлено добавить

    public Message(Long eventId, String text, MessageStatus messageStatus) {
        this.eventId = eventId;
        this.text = text;
        this.messageStatus = messageStatus;
    }
    // статус сообщения на accept reject

    public enum MessageStatus {
        ACCEPTED,
        DECLINED
    }

    public static MessageStatus parseStatusRequestToMessageStatus(StatusRequest statusRequest) {
        switch (statusRequest) {
            case ADDED_IN_CHAT:
                return Message.MessageStatus.ACCEPTED;
            case REJECTED_FROM_EVENT:
                return Message.MessageStatus.DECLINED;
            default:
                throw new IllegalArgumentException("Unsupported StatusRequest: " + statusRequest);
        }
    }
}
