package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventCuratorInfoDTO;
import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventCuratorInfo;
import org.bitebuilders.service.EventCuratorService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLOutput;
import java.util.List;

@RestController
@RequestMapping("/events_curators")
public class EventCuratorController {

    @Autowired
    private final EventCuratorService eventCuratorService;

    public EventCuratorController(EventCuratorService eventCuratorService) {
        this.eventCuratorService = eventCuratorService;
    }

    @GetMapping("/{eventId}/curators")
    public ResponseEntity<List<EventCuratorInfoDTO>> getCuratorsInfo(@PathVariable Long eventId) {
        List<EventCuratorInfoDTO> ecInfoDTO = eventCuratorService.getCuratorsInfo(eventId)
                .stream()
                .map(EventCuratorInfo::toEventCuratorDTO)
                .toList();

        if (ecInfoDTO != null)
            return ResponseEntity.ok(ecInfoDTO);  // Возвращаем список кураторов

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/waiting_curators")
    public ResponseEntity<List<EventCuratorInfoDTO>> getSentCuratorsInfo(@PathVariable Long eventId) {
        List<EventCuratorInfoDTO> ecInfoDTO = eventCuratorService.getSentCuratorInfo(eventId)
                .stream()
                .map(EventCuratorInfo::toEventCuratorDTO)
                .toList();

        if (ecInfoDTO != null)
            return ResponseEntity.ok(ecInfoDTO);  // Возвращаем список кураторов

        return ResponseEntity.noContent().build();
    }

    /**
     * Метод удаления заявки на кураторство. (reject)
     */
    @DeleteMapping("/{eventId}/delete/{curatorId}")
    public ResponseEntity<Boolean> deleteCuratorFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        return ResponseEntity.ok(
                eventCuratorService.updateCuratorStatus(
                        eventId,
                        curatorId,
                        StatusRequest.DELETED_FROM_EVENT
                )
        );
    }

    /**
     * Метод отправки заявки на кураторство. (reject)
     */
    @PutMapping("/{eventId}/send/{curatorId}")
    public ResponseEntity<Boolean> sendCuratorToEvent(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        Boolean isUpdated;
        try {
            isUpdated = eventCuratorService.updateCuratorStatus(
                    eventId,
                    curatorId,
                    StatusRequest.SENT_PERSONAL_INFO
            );
        } catch (EventUserNotFoundException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(isUpdated);
    }

    /**
     * Метод принятия заявки на кураторство. (reject)
     */
    @PutMapping("/{eventId}/accept/{curatorId}")
    public ResponseEntity<Boolean> acceptCuratorRequest(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        EventCurator eventCurator = eventCuratorService.getEventCurator(eventId, curatorId);
        if (eventCurator == null) {
            return ResponseEntity.notFound().build();
        } else if (eventCurator.getCuratorStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        boolean isUpdated = eventCuratorService.updateCuratorStatus(
                eventId,
                curatorId,
                StatusRequest.ADDED_IN_CHAT
        );

        return ResponseEntity.ok(isUpdated);
    } // TODO add curator to chat

    /**
     * Метод отклонения заявки на кураторство. (reject)
     */
    @PutMapping("/{eventId}/reject/{curatorId}")
    public ResponseEntity<Boolean> rejectCuratorRequest(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        EventCurator eventCurator = eventCuratorService.getEventCurator(eventId, curatorId);
        if (eventCurator == null) {
            return ResponseEntity.notFound().build();
        } else if (eventCurator.getCuratorStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        Boolean isUpdated = eventCuratorService.updateCuratorStatus(
                eventId,
                curatorId,
                StatusRequest.REJECTED_FROM_EVENT
        );

        return ResponseEntity.ok(isUpdated);
    }
}
