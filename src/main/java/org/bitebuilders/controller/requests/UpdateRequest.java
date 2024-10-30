package org.bitebuilders.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateRequest {
    private final String fieldName;
    private final Object newValue;
}
