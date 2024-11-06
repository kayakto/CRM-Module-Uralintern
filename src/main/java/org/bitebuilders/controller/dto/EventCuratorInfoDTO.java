package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class EventCuratorInfoDTO {
    private Long eventId;
    private Long curatorId;
    private String firstName;
    private String lastName;
    private String surname;
    private String telegramUrl;
    private String vkUrl;

    @Override
    public String toString() {
        return "EventCuratorInfoDTO{" +
                "eventId=" + eventId +
                ", curatorId=" + curatorId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", surname='" + surname + '\'' +
                ", telegramUrl='" + telegramUrl + '\'' +
                ", vkUrl='" + vkUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventCuratorInfoDTO that = (EventCuratorInfoDTO) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(curatorId, that.curatorId) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(surname, that.surname) && Objects.equals(telegramUrl, that.telegramUrl) && Objects.equals(vkUrl, that.vkUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, curatorId, firstName, lastName, surname, telegramUrl, vkUrl);
    }
}
