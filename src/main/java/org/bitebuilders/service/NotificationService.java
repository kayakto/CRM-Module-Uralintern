package org.bitebuilders.service;

import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.model.Message;
import org.bitebuilders.model.Notification;
import org.bitebuilders.model.NotificationInfo;
import org.bitebuilders.model.Event;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.MessageRepository;
import org.bitebuilders.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MessageService messageService;
    private final EventRepository eventRepository; // Для получения названия мероприятия

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               MessageService messageService,
                               EventRepository eventRepository) {
        this.notificationRepository = notificationRepository;
        this.messageService = messageService;
        this.eventRepository = eventRepository; // TODO переделать под сервисы
    }

    public List<NotificationInfo> getNotificationsByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        // Преобразование списка Notification в список NotificationInfo
        return notifications.stream().map(notification -> {
            Message message = messageService.getMessageById(notification.getMessageId());

            String eventTitle = eventRepository.findById(message.getEventId())
                    .map(Event::getTitle)
                    .orElseThrow(() -> new EventNotFoundException("Event does not exist"));;

            return new NotificationInfo(
                    notification.getId(),
                    eventTitle,
                    message.getText(),
                    notification.getSentAt()
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void sendNotification(Long userId, Long eventId, Message.MessageStatus messageStatus) {
        // Ищем сообщение по eventId и статусу
        Message message = messageService.getMessageByEventAndStatus(eventId, messageStatus);

        // Создаем уведомление с найденным сообщением
        Notification notification = new Notification(userId, message.getId(), OffsetDateTime.now());
        notificationRepository.save(notification);
    }
}
