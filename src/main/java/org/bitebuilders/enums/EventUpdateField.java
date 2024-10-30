package org.bitebuilders.enums;

public enum EventUpdateField {
    DESCRIPTION_TEXT("descriptionText"),
    TITLE("title"),
    MANAGER_ID("managerId"),
    EVENT_START_DATE("eventStartDate"),
    EVENT_END_DATE("eventEndDate"),
    CHAT_URL("chatUrl"),
    ENROLLMENT_START_DATE("enrollmentStartDate"),
    ENROLLMENT_END_DATE("enrollmentEndDate"),
    NUMBER_SEATS("numberSeats");

    private final String fieldName;

    EventUpdateField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
