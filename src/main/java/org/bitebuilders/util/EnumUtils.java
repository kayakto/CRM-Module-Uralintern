package org.bitebuilders.util;

import org.bitebuilders.enums.EventUpdateField;

public class EnumUtils {
    /**
     * Проверка, является ли fieldName допустимым полем для обновления
     * @param fieldName поле, которое проверяем
     * @return true, если поле допустимо для обновления
     */
    public static boolean isValidUpdateField(String fieldName) {
        for (EventUpdateField field : EventUpdateField.values()) {
            if (field.getFieldName().equals(fieldName)) {
                return true;
            }
        }

        return false;
    }
}
