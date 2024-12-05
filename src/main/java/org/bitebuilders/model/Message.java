package org.bitebuilders.model;

import lombok.Getter;
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
    @Column("message_text")
    private String messageText;
    @Column("edit_date")
    private OffsetDateTime editDate; // добавим таблицу сообщения когда было отправлено добавить
    // статус сообщения на accept reject
}
