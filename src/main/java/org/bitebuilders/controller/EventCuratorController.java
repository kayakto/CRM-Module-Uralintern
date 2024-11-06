package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventCuratorInfoDTO;
import org.bitebuilders.model.EventCuratorInfo;
import org.bitebuilders.service.EventCuratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
