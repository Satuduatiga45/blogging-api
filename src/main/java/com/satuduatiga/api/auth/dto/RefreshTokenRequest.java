package com.satuduatiga.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "refresh token must not be blank")
    private String refreshToken;
}
