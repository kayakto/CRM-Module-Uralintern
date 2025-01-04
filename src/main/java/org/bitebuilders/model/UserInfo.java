package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bitebuilders.controller.dto.UserDTO;
import org.bitebuilders.enums.UserRole;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

@Getter
@Table("users_info")
@Setter
@ToString
public class UserInfo {
    @Id
    private Long id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("surname")
    private String surname;
    @Column("email")
    private String email;
    @Column("sign")
    private String sign; // password
    @Column("telegram_url")
    private String telegramUrl;
    @Column("vk_url")
    private String vkUrl;
    @Column("role_enum")
    private UserRole role_enum;
    @Column("competencies")
    private String competencies;

    public UserInfo(String firstName, String lastName, String surname, String email, String sign, String vkUrl, String telegramUrl, UserRole role_enum, String competencies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.surname = surname;
        this.email = email;
        this.sign = sign;
        this.vkUrl = vkUrl;
        this.telegramUrl = telegramUrl;
        this.role_enum = role_enum;
        this.competencies = competencies;
    }

    public UserDTO toUserDTO() {
        return new UserDTO(
                this.id,
                this.firstName,
                this.lastName,
                this.surname,
                this.telegramUrl,
                this.vkUrl,
                this.role_enum,
                this.competencies
        );
    }
}
