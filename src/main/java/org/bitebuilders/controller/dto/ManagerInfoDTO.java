package org.bitebuilders.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class ManagerInfoDTO {
    private Long managerId;
    private String firstName;
    private String lastName;
    private String surname;
    private String email;
    private String telegramUrl;
    private String vkUrl;

    @Override
    public String toString() {
        return "ManagerInfoDto{" +
                "managerId=" + managerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", telegramUrl='" + telegramUrl + '\'' +
                ", vkUrl='" + vkUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagerInfoDTO that = (ManagerInfoDTO) o;
        return Objects.equals(managerId, that.managerId) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(surname, that.surname) && Objects.equals(email, that.email) && Objects.equals(telegramUrl, that.telegramUrl) && Objects.equals(vkUrl, that.vkUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managerId, firstName, lastName, surname, email, telegramUrl, vkUrl);
    }
}
