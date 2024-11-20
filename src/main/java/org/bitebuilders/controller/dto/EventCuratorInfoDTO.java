package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bitebuilders.enums.StatusRequest;

import java.util.Objects;

@Data
@AllArgsConstructor
public class EventCuratorInfoDTO {
    private Long eventId;
    private Long curatorId;
    private StatusRequest curatorStatus;
    private String firstName;
    private String lastName;
    private String surname;
    private String competencies;
    private String telegramUrl;
    private String vkUrl;

    @Override
    public String toString() {
        return "EventCuratorInfoDTO{" +
                "eventId=" + eventId +
                ", curatorId=" + curatorId +
                ", curatorStatus=" + curatorStatus +
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
        EventCuratorInfoDTO that = (EventCuratorInfoDTO) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(curatorId, that.curatorId) && curatorStatus == that.curatorStatus && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(surname, that.surname) && Objects.equals(competencies, that.competencies) && Objects.equals(telegramUrl, that.telegramUrl) && Objects.equals(vkUrl, that.vkUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, curatorId, curatorStatus, firstName, lastName, surname, competencies, telegramUrl, vkUrl);
    }
}
