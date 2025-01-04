package org.bitebuilders.service;

import org.bitebuilders.enums.StatusRequest;
import org.bitebuilders.exception.EventUserNotFoundException;
import org.bitebuilders.model.EventCurator;
import org.bitebuilders.model.EventGroup;
import org.bitebuilders.model.EventStudent;
import org.bitebuilders.repository.EventGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventGroupService {

    private final EventGroupRepository eventGroupRepository;

    private final EventCuratorService eventCuratorService;

    private final EventStudentService eventStudentService;

    @Autowired
    public EventGroupService(EventGroupRepository eventGroupRepository, EventCuratorService eventCuratorService, EventStudentService eventStudentService) {
        this.eventGroupRepository = eventGroupRepository;
        this.eventCuratorService = eventCuratorService;
        this.eventStudentService = eventStudentService;
    }

    public EventGroup save(EventGroup eventGroup) {
        return eventGroupRepository.save(eventGroup);
    }

    public List<EventGroup> getEventGroups(Long eventId) {
        return eventGroupRepository.findByEventId(eventId);
    }

    @Transactional
    public List<EventGroup> createGroups(Long eventId) {
        // проверки на существование event нет, так как ниже не найдет ни кураторов, ни студентов
        // и проверка event в вызывающих методах должна быть
        // Получение кураторов и студентов
        List<EventCurator> curators = eventCuratorService.getAcceptedEventCurator(eventId);
        int curatorsCount = curators.size();
        if (curatorsCount == 0) {
            throw new EventUserNotFoundException("No curators available for this event.");
        }

        List<EventStudent> students = eventStudentService.getAcceptedEventStudent(eventId);
        int studentsCount = students.size();
        if (studentsCount == 0) {
            throw new EventUserNotFoundException("No students available for this event.");
        } // Todo убери ошибки и ставь статус - ошибка создания меро

        System.out.println("есть и ивенты и студенты");

        // Определяем размер групп
        int baseGroupSize = studentsCount / curatorsCount;
        int remainingStudents = studentsCount % curatorsCount;

        // Результирующий список групп
        List<EventGroup> eventGroups = new ArrayList<>();

        // Индекс текущего студента
        int studentIndex = 0;

        // Распределение студентов по кураторам
        for (int i = 0; i < curatorsCount; i++) {
            // Создаем группу для текущего куратора и меняем его статус
            EventCurator curator = curators.get(i);
            curator.setCuratorStatus(StatusRequest.STARTED_EVENT);

            eventCuratorService.save(curator);

            // Создаем объект группы и добавляем его в список
            EventGroup group = new EventGroup(eventId, curator.getCuratorId());
            EventGroup savedGroup = save(group);
            eventGroups.add(group);

            int groupSize = baseGroupSize + (remainingStudents > 0 ? 1 : 0);
            Long groupId = savedGroup.getId();

            // Формируем группу студентов и меняем статусы студента
            for (int j = 0; j < groupSize; j++) {
                EventStudent student = students.get(studentIndex++);
                student.setStudentStatus(StatusRequest.STARTED_EVENT);
                student.setGroupId(groupId);
                // Сохраняем изменения студента
                eventStudentService.save(student);
            }

            // Уменьшаем счетчик оставшихся студентов
            if (remainingStudents > 0) {
                remainingStudents--;
            }
        }

        return eventGroups;
    }
}
