package ru.yandex.practicum.server.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Balance
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    private AtomicLong balance = new AtomicLong(0);

    public Balance(Long balance) {
        this.balance.set(balance);
    }

    /**
     * Get balance
     *
     * @return balance
     */
    @NotNull
    @Schema(name = "balance", example = "15000", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("balance")
    public Long getBalance() {
        return balance.get();
    }

    @JsonProperty("balance")
    public void setBalance(Long balance) {
        this.balance.set(balance);
    }

}

