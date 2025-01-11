package org.bitebuilders.controller;

import org.bitebuilders.controller.dto.EventStudentInfoDTO;
import org.bitebuilders.controller.dto.MessageResponseDTO;
import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.EventStudent;
import org.bitebuilders.model.EventStudentInfo;
import org.bitebuilders.service.EventService;
import org.bitebuilders.service.EventStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events-students")
public class EventStudentController {

    private final EventStudentService eventStudentService;

    private final EventService eventService;

    @Autowired
    public EventStudentController(EventStudentService eventStudentService, EventService eventService) {
        this.eventStudentService = eventStudentService;
        this.eventService = eventService;
    }

    // Получение всех студентов, отправивших персональные данные для участия
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{eventId}/students")
    public ResponseEntity<List<EventStudentInfoDTO>> getStudentsInfo(@PathVariable Long eventId) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        List<EventStudentInfoDTO> esInfoDTO = eventStudentService.getEventStudents(eventId)
                .stream()
                .map(EventStudentInfo::toEventStudentDTO)
                .toList();

        if (esInfoDTO != null)
            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов и их статусы

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{eventId}/student-can-send/{studentId}")
    public ResponseEntity<MessageResponseDTO> canSendStudent(
            @PathVariable Long eventId,
            @PathVariable Long studentId ) {
        boolean hasAbility = eventStudentService.canSend(eventId, studentId);

        return ResponseEntity.ok(
                new MessageResponseDTO(String.valueOf(hasAbility))
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{eventId}/waiting-students")
    public ResponseEntity<List<EventStudentInfoDTO>> getSentStudentsInfo(@PathVariable Long eventId) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        List<EventStudentInfoDTO> esInfoDTO = eventStudentService
                .getSentStudentInfo(eventId)
                .stream()
                .map(EventStudentInfo::toEventStudentDTO)
                .toList();

        if (esInfoDTO != null)
            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{eventId}/accepted-students")
    public ResponseEntity<List<EventStudentInfoDTO>> getAcceptedStudentsInfo(@PathVariable Long eventId) {
        if (!eventService.haveManagerAdminAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        List<EventStudentInfoDTO> esInfoDTO = eventStudentService
                .getAcceptedStudentInfo(eventId)
                .stream()
                .map(EventStudentInfo::toEventStudentDTO)
                .toList();

        if (esInfoDTO != null)
            return ResponseEntity.ok(esInfoDTO);  // Возвращаем список студентов

        return ResponseEntity.noContent().build();
    }

    /**
     * Метод отправки заявки студента на мероприятие
     */
    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/{eventId}/send/{studentId}")
    public ResponseEntity<MessageResponseDTO> sendStudentToEvent(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        boolean isUpdated = eventStudentService.updateStudentStatus(
                eventId,
                studentId,
                StatusRequest.SENT_PERSONAL_INFO
        );

        if (isUpdated) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Student with id " + studentId + " sent request to event " + eventId));
        }
        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Student with id " + studentId + " cannot send request to event " + eventId)
        ); // todo проверить, не закончились ли места
    }

    /**
     * Метод принятия заявки студента на мероприятие. (становится участником мероприятия)
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{eventId}/accept/{studentId}")
    public ResponseEntity<MessageResponseDTO> acceptStudentRequest(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        EventStudent eventStudent = eventStudentService.getEventStudent(eventId, studentId);
        if (eventStudent.getStudentStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        boolean isUpdated = eventStudentService.updateStudentStatus(
                eventId,
                studentId,
                StatusRequest.ADDED_IN_CHAT
        );

        if (isUpdated) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Student with id " + studentId + " accepted to event " + eventId));
        }
        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Student with id " + studentId + " cannot be accepted to event " + eventId)
        );
    } // TODO add student to chat

    /**
     * Метод отклонения заявки студента на мероприятие (reject)
     */
    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{eventId}/reject/{studentId}")
    public ResponseEntity<MessageResponseDTO> rejectStudentRequest(
            @PathVariable Long eventId,
            @PathVariable Long studentId
    ) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        EventStudent eventStudent = eventStudentService.getEventStudent(eventId, studentId);
        if (eventStudent.getStudentStatus() != StatusRequest.SENT_PERSONAL_INFO) {
            return ResponseEntity.badRequest().build();
        }

        boolean isUpdated = eventStudentService.updateStudentStatus(
                eventId,
                studentId,
                StatusRequest.REJECTED_FROM_EVENT
        );

        if (isUpdated) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Student with id " + studentId + " rejected from event " + eventId));
        }
        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Student with id " + studentId + " cannot be rejected from event " + eventId)
        );
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/change-curator/{eventId}/students/{studentId}/curator/{newCuratorId}")
    public ResponseEntity<MessageResponseDTO> changeStudentCurator(
            @PathVariable Long eventId,
            @PathVariable Long studentId,
            @RequestParam Long newCuratorId
    ) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            eventStudentService.changeCurator(eventId, studentId, newCuratorId);
            return ResponseEntity.ok(
                    new MessageResponseDTO("Curator " + newCuratorId + " updated for student: " + studentId)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new MessageResponseDTO(e.getMessage()));
        }
    } // todo еще потестить

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{eventId}/delete/{studentId}")
    public ResponseEntity<MessageResponseDTO> deleteStudentFromEvent(
            @PathVariable Long eventId,
            @PathVariable Long studentId) {
        if (!eventService.haveManagerAccess(eventId)) {
            return ResponseEntity.badRequest().build();
        }

        boolean isDeleted = eventStudentService
                .updateStudentStatus(
                        eventId,
                        studentId,
                        StatusRequest.DELETED_FROM_EVENT);

        if (isDeleted) {
            return ResponseEntity.ok(
                    new MessageResponseDTO("Student " + studentId + " successfully deleted from event " + eventId)
            );
        }

        return ResponseEntity.badRequest().body(
                new MessageResponseDTO("Student " + studentId + " cannot be deleted from event " + eventId)
        );
    }
}
