package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.bitebuilders.enums.StatusRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("events_students")
public class EventStudent {
    @Id
    private Long id;
    @Column("student_id")
    private Long studentId;
    @Column("event_id")
    private Long eventId;
    @Column("student_status")
    @Setter
    private StatusRequest studentStatus;
    @Column("group_id")
    @Setter
    private Long groupId;

    public EventStudent(Long studentId, Long eventId, StatusRequest studentStatus) {
        this.studentId = studentId;
        this.eventId = eventId;
        this.studentStatus = studentStatus;
    }
}
