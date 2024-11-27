package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bitebuilders.enums.StatusRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("events_curators")
@Setter
public class EventCurator {
    @Id
    private Long id;
    @Column("curator_id")
    private Long curatorId;
    @Column("event_id")
    private Long eventId;
    @Column("curator_status")
    @Setter
    private StatusRequest curatorStatus;

    public EventCurator(Long curatorId, Long eventId, StatusRequest curatorStatus) {
        this.curatorId = curatorId;
        this.eventId = eventId;
        this.curatorStatus = curatorStatus;
    }
}
