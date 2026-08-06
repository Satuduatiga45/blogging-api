package com.satuduatiga.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "username must not be blank")
    private String username;

    @NotBlank(message = "email must not be blank")
    @Email(message = "email address format is invalid")
    private String email;

    @NotBlank(message = "password must not be blank")
    @Min(value = 8, message = "password must be at least 8 character")
    private String password;

}
