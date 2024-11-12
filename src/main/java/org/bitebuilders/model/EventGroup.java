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
}
