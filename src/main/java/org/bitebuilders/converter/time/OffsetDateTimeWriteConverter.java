package org.bitebuilders.converter.time;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OffsetDateTimeWriteConverter implements Converter<OffsetDateTime, String> {
    @Override
    public String convert(OffsetDateTime source) {
        // Преобразуем OffsetDateTime в ISO-строку с временной зоной
        return source != null ? source.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null;
    }
}
