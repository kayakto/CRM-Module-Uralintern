package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("event_groups")
@Getter
public class EventGroup {
    @Id
    private Long id;
    @Column("event_id")
    private Long eventId;
    @Column("curator_id")
    private Long curatorId;

    public EventGroup(Long eventId, Long curatorId) {
        this.eventId = eventId;
        this.curatorId = curatorId;
    }

    @Override
    public String toString() {
        return "EventGroup{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", curatorId=" + curatorId +
                '}';
    }
}
