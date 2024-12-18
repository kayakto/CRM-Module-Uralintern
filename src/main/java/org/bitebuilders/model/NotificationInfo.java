package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class NotificationInfo {
    public Long notificationId;
    public String eventTitle;
    public String messageText;
    public OffsetDateTime sent_at;
}
