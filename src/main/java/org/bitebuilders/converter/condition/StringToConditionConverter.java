package org.bitebuilders.converter.condition;

import org.bitebuilders.model.Event;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;


@Component
@ReadingConverter
public class StringToConditionConverter implements Converter<String, Event.Condition> {
    @Override
    public Event.Condition convert(String source) {
        return Event.Condition.valueOf(source);
    }
}
