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

    private AtomicLong sum = new AtomicLong(0);

    public Balance(Long sum) {
        this.sum.set(sum);
    }

    /**
     * Get balance
     *
     * @return balance
     */
    @NotNull
    @Schema(name = "balance", example = "15000", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("balance")
    public Long getSum() {
        return sum.get();
    }

    @JsonProperty("balance")
    public void setSum(Long balance) {
        this.sum.set(balance);
    }

}

