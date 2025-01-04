package org.bitebuilders.service;

import org.bitebuilders.exception.EventNotFoundException;
import org.bitebuilders.exception.MessageNotFoundException;
import org.bitebuilders.model.Event;
import org.bitebuilders.model.Message;
import org.bitebuilders.repository.EventRepository;
import org.bitebuilders.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    private final EventRepository eventRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, EventRepository eventRepository) {
        this.messageRepository = messageRepository;
        this.eventRepository = eventRepository;
    }

    public Message getMessageById(Long messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            return optionalMessage.get();
        }
        throw new MessageNotFoundException("Message with id " + messageId + " not found");
    }

    public List<Message> getEventMessages(Long eventId) {
        List<Message> messages = messageRepository.findByEventId(eventId);
        if (messages.isEmpty()){
            createDefaultMessages(eventId);
            messages = messageRepository.findByEventId(eventId);
        }
        return messages;
    }

    public Message getMessageByEventAndStatus(Long eventId, Message.MessageStatus messageStatus) {
        Optional<Message> optionalMessage = messageRepository.findMessageByEventAndStatus(eventId, messageStatus);
        if (optionalMessage.isEmpty()) {
            createDefaultMessages(eventId);
        }
        return messageRepository.findMessageByEventAndStatus(eventId, messageStatus).get();
    }

    @Transactional
    public Message addMessage(Long eventId, String messageText, Message.MessageStatus messageStatus) {
        Message newMessage = new Message(eventId, messageText, messageStatus);
        return messageRepository.save(newMessage);
    } // пока не используется

    @Transactional
    public Message updateMessage(Message message, String messageText) {
        message.setText(messageText);
        message.setEditDate(OffsetDateTime.now());
        return messageRepository.save(message);
    }

    @Transactional
    public void createDefaultMessages(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id: " + eventId + " not found"));

        // Создаем сообщения
        String acceptText = "Вы приняты на мероприятие \"" + event.getTitle() + "\". Присоединяйтесь к чату: " + event.getChatUrl();
        String declineText = "К сожалению, ваша заявка на мероприятие \"" + event.getTitle() + "\". была отклонена.";

        messageRepository.save(new Message(eventId, acceptText, Message.MessageStatus.ACCEPTED));
        messageRepository.save(new Message(eventId, declineText, Message.MessageStatus.DECLINED));
    }
}
