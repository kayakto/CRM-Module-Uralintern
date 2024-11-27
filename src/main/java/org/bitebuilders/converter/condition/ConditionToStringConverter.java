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
        if (condition == null) {
            throw new IllegalArgumentException("Condition can`t be null");
        }
        return condition.name();
    }
}


