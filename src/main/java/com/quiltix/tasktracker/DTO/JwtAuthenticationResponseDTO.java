
package com.quiltix.tasktracker.DTO;



import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT")
public class JwtAuthenticationResponseDTO {
    @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1...")
    private String accessToken;
    @Schema(description = "Тип аутентификации", example = "Bearer")
    private String tokenType = "Bearer";

    public JwtAuthenticationResponseDTO(String accessToken) {
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
