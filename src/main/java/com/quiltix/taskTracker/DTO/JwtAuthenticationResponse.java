package com.quiltix.taskTracker.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
@Schema(description = "Ответ с JWT")
public class JwtAuthenticationResponse {
    @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1...")
    private String accessToken;
    @Schema(description = "Тип аутентификации", example = "Bearer")
    private String tokenType = "Bearer";

    public JwtAuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // Геттеры (и, если нужно, сеттеры)
    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }
}
