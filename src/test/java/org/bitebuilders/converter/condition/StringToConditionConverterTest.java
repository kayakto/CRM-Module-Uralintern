package org.bitebuilders.converter.condition;

import org.bitebuilders.model.Event.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

public class StringToConditionConverterTest {

    private final Converter<String, Condition> converter = new StringToConditionConverter();

    @Test
    public void testConverter() {
        // Arrange
        String input = "REGISTRATION_OPEN";

        // Act
        Condition result = converter.convert(input);

        // Assert
        assertEquals(Condition.REGISTRATION_OPEN, result);
    }

    @Test
    public void testConverterAnotherValue() {
        // Arrange
        String input = "HIDDEN";

        // Act
        Condition result = converter.convert(input);

        // Assert
        assertEquals(Condition.HIDDEN, result);
    }

    @Test
    public void testConverterNull() {
        // Arrange
        String input = null;

        // Act and Assert
        assertThrows(
                NullPointerException.class,
                () -> converter.convert(input));
    }

    @Test
    public void testConvertNotExistingValue() {
        // Arrange
        String input = "NOT_EXISTING_VALUE";

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(input));
    }
}
