package org.bitebuilders.converter.condition;

import org.bitebuilders.model.Event.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionToStringConverterTest {

    private final Converter<Condition, String> converter = new ConditionToStringConverter();

    @Test
    public void testConvert() {
        // Arrange
        Condition input = Condition.REGISTRATION_OPEN;

        // Act
        String result = converter.convert(input);

        // Assert
        assertEquals("REGISTRATION_OPEN", result);
    }

    @Test
    public void testConvertForAnotherValue() {
        // Arrange
        Condition input = Condition.HIDDEN;

        // Act
        String result = converter.convert(input);

        // Assert
        assertEquals("HIDDEN", result);
    }

    @Test
    public void testConvertNull() {
        // Arrange
        Condition input = null;

        // Act and Assert
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(input));

        String expectedMessage = "Condition can`t be null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
