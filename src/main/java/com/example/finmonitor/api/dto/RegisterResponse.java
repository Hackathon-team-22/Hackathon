package com.example.finmonitor.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Ответ при успешной регистрации")
public class RegisterResponse {

    @Schema(description = "Сообщение об успешной регистрации", example = "User registered successfully")
    private String message;

    public RegisterResponse() {}

    public RegisterResponse(String message) {
        this.message = message;
    }

}
