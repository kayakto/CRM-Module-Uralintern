package org.bitebuilders.config;

import org.bitebuilders.model.Event;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class ConditionToPostgresConverter implements Converter<Event.Condition, PGobject> {

    @Override
    public PGobject convert(Event.Condition condition) {
        PGobject pgObject = new PGobject();
        pgObject.setType("event_condition");
        try {
            pgObject.setValue(condition.name());
        } catch (SQLException e) {
            throw new RuntimeException("Error setting value for PostgreSQL ENUM", e);
        }
        return pgObject;
    }
}
