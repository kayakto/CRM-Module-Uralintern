package org.bitebuilders.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bitebuilders.controller.dto.EventCuratorInfoDTO;
import org.bitebuilders.enums.StatusRequest;
import org.springframework.data.relational.core.mapping.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventCuratorInfo {
    @Column("event_id")
    private Long eventId;
    @Column("curator_id")
    private Long curatorId;
    @Column("curator_status")
    private StatusRequest curatorStatus;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("surname")
    private String surname;
    @Column("competencies")
    private String competencies;
    @Column("telegram_url")
    private String telegramUrl;
    @Column("vk_url")
    private String vkUrl;

    public EventCuratorInfoDTO toEventCuratorDTO() {
        return new EventCuratorInfoDTO(
                this.getEventId(),
                this.getCuratorId(),
                this.getCuratorStatus(),
                this.getFirstName(),
                this.getLastName(),
                this.getSurname(),
                this.getCompetencies(),
                this.getTelegramUrl(),
                this.getVkUrl()
        );
    }
}
