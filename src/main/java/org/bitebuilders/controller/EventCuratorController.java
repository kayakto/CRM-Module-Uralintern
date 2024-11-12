package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventCuratorInfoDTO;
import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.EventCuratorInfo;
import org.bitebuilders.service.EventCuratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        List<EventCuratorInfoDTO> ecInfoDTO = eventCuratorService.getCuratorInfo(eventId)
                .stream()
                .map(EventCuratorInfo::toEventCuratorDTO)
                .toList();
        if (ecInfoDTO != null)
            return ResponseEntity.ok(ecInfoDTO);  // Возвращаем список кураторов
        return ResponseEntity.noContent().build();
    }

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
}
