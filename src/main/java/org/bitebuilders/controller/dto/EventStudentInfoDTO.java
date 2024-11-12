package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bitebuilders.enums.StatusRequest;

import java.util.Objects;

@Data
@AllArgsConstructor
public class EventStudentInfoDTO {
    private Long eventId;
    private Long studentId;
    private StatusRequest statusRequest;
    private String firstName;
    private String lastName;
    private String surname;
    private String competencies;
    private String telegramUrl;
    private String vkUrl;
    private String curatorFirstName;
    private String curatorLastName;
    private String curatorSurname;

    @Override
    public String toString() {
        return "EventStudentInfoDTO{" +
                "eventId=" + eventId +
                ", studentId=" + studentId +
                ", statusRequest=" + statusRequest +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", surname='" + surname + '\'' +
                ", competencies='" + competencies + '\'' +
                ", telegramUrl='" + telegramUrl + '\'' +
                ", vkUrl='" + vkUrl + '\'' +
                ", curatorFirstName='" + curatorFirstName + '\'' +
                ", curatorLastName='" + curatorLastName + '\'' +
                ", curatorSurname='" + curatorSurname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventStudentInfoDTO that = (EventStudentInfoDTO) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(studentId, that.studentId) && statusRequest == that.statusRequest && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(surname, that.surname) && Objects.equals(competencies, that.competencies) && Objects.equals(telegramUrl, that.telegramUrl) && Objects.equals(vkUrl, that.vkUrl) && Objects.equals(curatorFirstName, that.curatorFirstName) && Objects.equals(curatorLastName, that.curatorLastName) && Objects.equals(curatorSurname, that.curatorSurname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, studentId, statusRequest, firstName, lastName, surname, competencies, telegramUrl, vkUrl, curatorFirstName, curatorLastName, curatorSurname);
    }
}
