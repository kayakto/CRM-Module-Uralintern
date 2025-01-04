package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventCuratorInfoDTO;
import org.bitebuilders.controller.dto.MessageResponseDTO;
import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventCuratorInfo;
import org.bitebuilders.service.EventCuratorService;
import org.bitebuilders.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events-curators")
public class EventCuratorController {

    private final EventCuratorService eventCuratorService;

    private final EventService eventService;

    @Autowired
    public EventCuratorController(EventCuratorService eventCuratorService, EventService eventService) {
        this.eventCuratorService = eventCuratorService;
        this.eventService = eventService;
    }

    @GetMapping("/{eventId}/curators")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<EventCuratorInfoDTO>> getCuratorsInfo(@PathVariable Long eventId) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        List<EventCuratorInfoDTO> ecInfoDTO = eventCuratorService.getCuratorsInfo(eventId)
                .stream()
                .map(EventCuratorInfo::toEventCuratorDTO)
                .toList();

        if (ecInfoDTO != null)
            return ResponseEntity.ok(ecInfoDTO);  // Возвращаем список кураторов

        return ResponseEntity.noContent().build();
    } // TODO может быть удалено

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{eventId}/waiting-curators")
    public ResponseEntity<List<EventCuratorInfoDTO>> getSentCuratorsInfo(@PathVariable Long eventId) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        List<EventCuratorInfoDTO> ecInfoDTO = eventCuratorService.getSentCuratorInfo(eventId)
                .stream()
                .map(EventCuratorInfo::toEventCuratorDTO)
                .toList();

        if (ecInfoDTO != null)
            return ResponseEntity.ok(ecInfoDTO);  // Возвращаем список кураторов

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{eventId}/accepted-curators")
    public ResponseEntity<List<EventCuratorInfoDTO>> getAcceptedCuratorsInfo(@PathVariable Long eventId) {
        if (!eventService.haveManagerAdminAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        List<EventCuratorInfoDTO> ecInfoDTO = eventCuratorService.getAcceptedCuratorInfo(eventId)
                .stream()
                .map(EventCuratorInfo::toEventCuratorDTO)
                .toList();

        if (ecInfoDTO != null)
            return ResponseEntity.ok(ecInfoDTO);  // Возвращаем список кураторов

        return ResponseEntity.noContent().build();
    }

    /**
     * Метод отправки заявки на кураторство. (reject)
     */
    @PreAuthorize("hasRole('CURATOR')")
    @PutMapping("/{eventId}/send/{curatorId}")
    public ResponseEntity<MessageResponseDTO> sendCuratorToEvent(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        boolean isUpdated = eventCuratorService.updateCuratorStatus(
                eventId,
                curatorId,
                StatusRequest.SENT_PERSONAL_INFO
        );

        if (isUpdated) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Curator with id " + curatorId + " send request to event " + eventId));
        }
        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Curator with id " + curatorId + " cannot send request to event " + eventId)
        );
    }

    /**
     * Метод принятия заявки на кураторство. (reject)
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{eventId}/accept/{curatorId}")
    public ResponseEntity<MessageResponseDTO> acceptCuratorRequest(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        EventCurator eventCurator = eventCuratorService.getEventCurator(eventId, curatorId);
        if (eventCurator.getCuratorStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        boolean isUpdated = eventCuratorService.updateCuratorStatus(
                eventId,
                curatorId,
                StatusRequest.ADDED_IN_CHAT
        );

        if (isUpdated) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Curator with id " + curatorId + " accepted to event " + eventId));
        }
        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Curator with id " + curatorId + " cannot be accepted to event " + eventId)
        );
    } // TODO add curator to chat

    /**
     * Метод отклонения заявки на кураторство. (reject)
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{eventId}/reject/{curatorId}")
    public ResponseEntity<MessageResponseDTO> rejectCuratorRequest(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        EventCurator eventCurator = eventCuratorService.getEventCurator(eventId, curatorId);
        if (eventCurator.getCuratorStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        boolean isUpdated = eventCuratorService.updateCuratorStatus(
                eventId,
                curatorId,
                StatusRequest.REJECTED_FROM_EVENT
        );

        if (isUpdated) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Curator with id " + curatorId + " rejected to event " + eventId));
        }
        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Curator with id " + curatorId + " cannot be rejected to event " + eventId)
        );
    }

    /**
     * Метод удаления куратора с мероприятия
     */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{eventId}/delete/{curatorId}")
    public ResponseEntity<MessageResponseDTO> deleteCuratorFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long curatorId
    ) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        boolean isDeleted = eventCuratorService.updateCuratorStatus(
                eventId,
                curatorId,
                StatusRequest.DELETED_FROM_EVENT
        );

        if (isDeleted) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Curator with id " + curatorId + " deleted successfully from event " + eventId));
        }
        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Curator with id " + curatorId + " cannot delete from event " + eventId)
        );
    }
}
