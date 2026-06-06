package ru.yadex.practicum.server.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Balance
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    private Long balance;

    /**
     * Get balance
     *
     * @return balance
     */
    @NotNull
    @Schema(name = "balance", example = "15000", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("balance")
    public Long getBalance() {
        return balance;
    }

    @JsonProperty("balance")
    public void setBalance(Long balance) {
        this.balance = balance;
    }

}

