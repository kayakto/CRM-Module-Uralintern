package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bitebuilders.StudentStatus;
import org.bitebuilders.controller.dto.EventStudentInfoDTO;
import org.springframework.data.relational.core.mapping.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventStudentInfo {
    @Column("event_id")
    private Long eventId;
    @Column("student_id")
    private Long studentId;
    @Column("student_status")
    private StudentStatus studentStatus;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("surname")
    private String surname;
    @Column("competencies")
    private String competencies;
    @Column("telegram_url")
    private String telegramUrl;
    @Column("vk_url")
    private String vkUrl;

    public EventStudentInfoDTO toEventStudentDTO() {
        return new EventStudentInfoDTO(
                this.getEventId(),
                this.getStudentId(),
                this.getStudentStatus(),
                this.getFirstName(),
                this.getLastName(),
                this.getSurname(),
                this.getCompetencies(),
                this.getTelegramUrl(),
                this.getVkUrl()
        );
    }
}
