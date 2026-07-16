package com.satuduatiga.api.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String identifier; // username or email
    private String password;

}
