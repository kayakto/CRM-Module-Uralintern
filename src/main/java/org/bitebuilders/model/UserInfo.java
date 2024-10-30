package org.bitebuilders.model;

import lombok.Getter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

@Getter
@Table("users_info")
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

    public enum Role {
        ADMIN, CURATOR, MANAGER, STUDENT
    }
}
