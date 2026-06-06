package ru.yadex.practicum.server.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Error
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Error {

    private @Nullable String message;

    /**
     * Get message
     *
     * @return message
     */

    @Schema(name = "message", example = "Сообщение об ошибке", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("message")
    public @Nullable String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(@Nullable String message) {
        this.message = message;
    }

}

