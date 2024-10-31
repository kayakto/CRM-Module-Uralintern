package org.bitebuilders.model;

import lombok.Getter;
import org.bitebuilders.controller.dto.StudentStatusDTO;
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
    private StudentStatus studentStatus;

    public enum StudentStatus {
        SENT_PERSONAL_INFO,
        ADDED_IN_CHAT,
        STARTED_EVENT,
        ENDED_EVENT,
        DELETED_FROM_EVENT
    }

    public StudentStatusDTO toStudentStatusDTO() {
        return new StudentStatusDTO(studentId, studentStatus);
    }
}
