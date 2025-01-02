package org.bitebuilders.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class LoginRequest {
    public String email;
    public String password;
}
