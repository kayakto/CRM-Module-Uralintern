package org.bitebuilders.converter.condition;

import org.bitebuilders.model.Event;
import org.springframework.core.convert.converter.Converter;

import org.springframework.stereotype.Component;

@Component
public class ConditionToStringConverter implements Converter<Event.Condition, String> {
    @Override
    public String convert(Event.Condition condition) {
        System.out.println("Converting Condition to String: " + condition);
        return condition.name();
    }
}

