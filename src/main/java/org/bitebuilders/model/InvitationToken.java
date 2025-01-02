package org.bitebuilders.model;

import lombok.Getter;
import lombok.Setter;
import org.bitebuilders.enums.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Getter
@Table("invitations")
public class InvitationToken {
    @Id
    private Long id;

    @Column("token")
    private String token; // Уникальный токен

    @Column("role_enum")
    private UserRole role; // Роль, для которой предназначена ссылка

    @Column("expiration_Date")
    private Date expirationDate; // Дата истечения срока действия

    @Column("used")
    @Setter
    private Boolean used = false; // Флаг, использована ли ссылка

    public InvitationToken(String token, UserRole role, Date expirationDate) {
        this.token = token;
        this.role = role;
        this.expirationDate = expirationDate;
    }

    public boolean isUsed() {
        return this.used;
    }
}
