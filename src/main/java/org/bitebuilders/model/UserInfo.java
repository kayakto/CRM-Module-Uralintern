package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bitebuilders.controller.dto.ManagerInfoDTO;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

@Getter
@Table("users_info")
@Setter
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
    private Role role_enum;
    @Column("competencies")
    private String competencies;

    public UserInfo(String firstName, String lastName, String surname, String email, String sign, String vkUrl, String telegramUrl, Role role_enum, String competencies) {
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

    public enum Role {
        ADMIN, CURATOR, MANAGER, STUDENT
    }

    public ManagerInfoDTO toManagerInfoDTO() {
        return new ManagerInfoDTO(
                this.id,
                this.firstName,
                this.lastName,
                this.surname,
                this.email,
                this.telegramUrl,
                this.vkUrl
        );
    }
}
