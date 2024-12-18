package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Getter
@Table("notifications")
public class Notification {
    @Id
    private Long id;
    @Column("user_id")
    private Long userId; // Ссылка на пользователя
    @Column("message_id")
    private Long messageId; // Само сообщение
    @Column("sent_at")
    private OffsetDateTime sentAt;

    public Notification(Long userId, Long messageId, OffsetDateTime sentAt) {
        this.userId = userId;
        this.messageId = messageId;
        this.sentAt = sentAt;
    }
}
