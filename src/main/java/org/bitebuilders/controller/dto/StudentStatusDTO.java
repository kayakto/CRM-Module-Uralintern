package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bitebuilders.model.EventStudent;

@AllArgsConstructor
@Getter
public class StudentStatusDTO {
    private Long studentId;
    private EventStudent.StudentStatus studentStatus;

    public StudentStatusDTO(EventStudent eventstudent) {
        this.studentId = eventstudent.getStudentId();
        this.studentStatus = eventstudent.getStudentStatus();
    }
}
