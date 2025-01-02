package org.bitebuilders.controller.dto;

import lombok.*;
import org.bitebuilders.enums.UserRole;

@Data
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String surname;
    private String telegramUrl;
    private String vkUrl;
    private UserRole role_enum;
    private String competencies;
}
