package org.bitebuilders.converter.condition;

import org.bitebuilders.model.Event;
import org.springframework.core.convert.converter.Converter;

import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter
public class ConditionToStringConverter implements Converter<Event.Condition, String> {
    @Override
    public String convert(Event.Condition condition) {
        return condition.name();
    }
}


