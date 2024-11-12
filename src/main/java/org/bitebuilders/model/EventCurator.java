package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.bitebuilders.enums.StatusRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("events_curators")
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
}
