package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bitebuilders.StudentStatus;

import java.util.Objects;

@Data
@AllArgsConstructor
public class EventStudentInfoDTO {
    private Long eventId;
    private Long studentId;
    private StudentStatus studentStatus;
    private String firstName;
    private String lastName;
    private String surname;
    private String competencies;
    private String telegramUrl;
    private String vkUrl;

    @Override
    public String toString() {
        return "EventStudentInfoDTO{" +
                "eventId=" + eventId +
                ", studentId=" + studentId +
                ", studentStatus=" + studentStatus +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", surname='" + surname + '\'' +
                ", competencies='" + competencies + '\'' +
                ", telegramUrl='" + telegramUrl + '\'' +
                ", vkUrl='" + vkUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStudentInfoDTO that = (EventStudentInfoDTO) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(studentId, that.studentId) && studentStatus == that.studentStatus && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(surname, that.surname) && Objects.equals(competencies, that.competencies) && Objects.equals(telegramUrl, that.telegramUrl) && Objects.equals(vkUrl, that.vkUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, studentId, studentStatus, firstName, lastName, surname, competencies, telegramUrl, vkUrl);
    }
}
