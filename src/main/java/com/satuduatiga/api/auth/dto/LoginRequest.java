package com.satuduatiga.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "you must enter your username or email")
    private String identifier; // username or email

    @NotBlank(message = "password must not be blank")
    private String password;

}
