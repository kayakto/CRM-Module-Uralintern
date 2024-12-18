package org.bitebuilders.converter.messageStatus;

import org.bitebuilders.model.Message;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class StringToMessageStatusConverter implements Converter<String, Message.MessageStatus> {

    @Override
    public Message.MessageStatus convert(String source) {
        return Message.MessageStatus.valueOf(source);
    }
}
