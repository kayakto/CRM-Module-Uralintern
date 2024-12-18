package org.bitebuilders.converter.messageStatus;

import org.bitebuilders.model.Message;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class MessageStatusToStringConverter implements Converter<Message.MessageStatus, String> {
    @Override
    public String convert(Message.MessageStatus messageStatus) {
        if (messageStatus == null) {
            throw new IllegalArgumentException("message status can`t be null");
        }
        return messageStatus.name();
    }
}
