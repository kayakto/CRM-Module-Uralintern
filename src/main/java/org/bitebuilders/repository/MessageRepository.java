package org.bitebuilders.repository;

import org.bitebuilders.model.Message;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    Optional<Message> findById(Long id);

    List<Message> findByEventId(Long eventId);

    @Query("SELECT * FROM messages WHERE event_id = :eventId AND status = :messageStatus;")
    Optional<Message> findMessageByEventAndStatus(Long eventId, Message.MessageStatus messageStatus);
}
