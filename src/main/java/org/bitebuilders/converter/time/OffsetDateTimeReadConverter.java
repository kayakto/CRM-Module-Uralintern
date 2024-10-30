package org.bitebuilders.converter.time;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class OffsetDateTimeReadConverter implements Converter<Timestamp, OffsetDateTime> {
    @Override
    public OffsetDateTime convert(Timestamp source) {
        return source != null ? source.toInstant().atOffset(ZoneOffset.UTC) : null;
    } // работает
}

