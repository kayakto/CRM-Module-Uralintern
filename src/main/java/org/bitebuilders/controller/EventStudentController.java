package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventStudentInfoDTO;
import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.model.EventStudentInfo;
import org.bitebuilders.service.EventStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events_students")
public class EventStudentController {

    @Autowired
    private final EventStudentService eventStudentService;

    public EventStudentController(EventStudentService eventStudentService) {
        this.eventStudentService = eventStudentService;
    }

    // Получение всех студентов, отправивших персональные данные для участия
    @GetMapping("/{eventId}/students")
    public ResponseEntity<List<EventStudentInfoDTO>> getStudentsEvent(@PathVariable Long eventId) {
        List<EventStudentInfoDTO> esInfoDTO = eventStudentService.getEventStudents(eventId)
                .stream()
                .map(EventStudentInfo::toEventStudentDTO)
                .toList();
        if (esInfoDTO != null)
            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов и их статусы
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/delete/{studentId}")
    public ResponseEntity<Boolean> deleteStudentFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(
                eventStudentService
                        .updateStudentStatus(
                                eventId,
                                studentId,
                                StatusRequest.DELETED_FROM_EVENT));
    }
}
