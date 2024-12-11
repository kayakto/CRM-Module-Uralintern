package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventStudentInfoDTO;
import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.EventStudent;
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
    public ResponseEntity<List<EventStudentInfoDTO>> getStudentsInfo(@PathVariable Long eventId) {
        List<EventStudentInfoDTO> esInfoDTO = eventStudentService.getEventStudents(eventId)
                .stream()
                .map(EventStudentInfo::toEventStudentDTO)
                .toList();

        if (esInfoDTO != null)
            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов и их статусы

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/waiting_students")
    public ResponseEntity<List<EventStudentInfoDTO>> getSentStudentsInfo(@PathVariable Long eventId) {
        List<EventStudentInfoDTO> esInfoDTO = eventStudentService.getSentStudentInfo(eventId)
                .stream()
                .map(EventStudentInfo::toEventStudentDTO)
                .toList();

        if (esInfoDTO != null)
            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов

        return ResponseEntity.noContent().build();
    }

    // Получение статуса студента GET - возвращает текущий статус студента или куратора.
    @GetMapping("/{eventId}/student-status/{studentId}")
    public ResponseEntity<StatusRequest> getStudentStatus(Long eventId, Long studentId) {
        StatusRequest result = eventStudentService.getStudentStatus(eventId, studentId);
        return ResponseEntity.ok(result);
    }

    // Ручное изменение статуса студента - позволяет руководителю вручную изменять статус участника мероприятия (“Удалён(а) с мероприятия“)
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

    /**
     * Метод отправки заявки студента на мероприятие
     */
    @PutMapping("/{eventId}/send/{studentId}")
    public ResponseEntity<Boolean> sendStudentToEvent(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        Boolean isUpdated;
        try {
            isUpdated = eventStudentService.updateStudentStatus(
                    eventId,
                    studentId,
                    StatusRequest.SENT_PERSONAL_INFO
            );
        } catch (EventUserNotFoundException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(isUpdated); // todo проверить, не закончились ли места
    }

    /**
     * Метод принятия заявки студента на мероприятие. (становится участником мероприятия)
     */
    @PutMapping("/{eventId}/accept/{studentId}")
    public ResponseEntity<Boolean> acceptStudentRequest(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        EventStudent eventStudent = eventStudentService.getEventStudent(eventId, studentId);
        if (eventStudent == null) {
            return ResponseEntity.notFound().build();
        } else if (eventStudent.getStudentStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        Boolean isUpdated = eventStudentService.updateStudentStatus(
                eventId,
                studentId,
                StatusRequest.ADDED_IN_CHAT
        );

        return ResponseEntity.ok(isUpdated);
    } // TODO add student to chat

    /**
     * Метод отклонения заявки студента на мероприятие (reject)
     */
    @PutMapping("/{eventId}/reject/{studentId}")
    public ResponseEntity<Boolean> rejectStudentRequest(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        EventStudent eventStudent = eventStudentService.getEventStudent(eventId, studentId);
        if (eventStudent == null) {
            return ResponseEntity.notFound().build();
        } else if (eventStudent.getStudentStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        Boolean isUpdated = eventStudentService.updateStudentStatus(
                eventId,
                studentId,
                StatusRequest.REJECTED_FROM_EVENT
        );

        return ResponseEntity.ok(isUpdated);
    }

    @PutMapping("/change-curator/{eventId}/students/{studentId}/curator/{newCuratorId}")
    public ResponseEntity<String> changeStudentCurator(
            @PathVariable Long eventId,
            @PathVariable Long studentId,
            @RequestParam Long newCuratorId
    ) {
        try {
            eventStudentService.changeCurator(eventId, studentId, newCuratorId);
            return ResponseEntity.ok("Curator successfully updated for student: " + studentId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    } // todo еще потестить
}
