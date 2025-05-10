
package com.quiltix.tasktracker.dto.auth;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Ответ с JWT")
public class JwtAuthenticationResponseDTO {
    @Schema(description = "JWT токен", example = "eyJhbGciOiJIUzI1...")
    private String accessToken;
    @Schema(description = "Тип аутентификации", example = "Bearer")
    private final String tokenType = "Bearer";

    public JwtAuthenticationResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
