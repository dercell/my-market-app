package ru.yadex.practicum.server.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ChargeBalanceRequest
 */

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeName("chargeBalance_request")
public class ChargeBalanceRequest {

    private Long totalSum;

    /**
     * Get totalSum
     *
     * @return totalSum
     */
    @NotNull
    @Schema(name = "totalSum", example = "4000", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("totalSum")
    public Long getTotalSum() {
        return totalSum;
    }

    @JsonProperty("totalSum")
    public void setTotalSum(Long totalSum) {
        this.totalSum = totalSum;
    }

}

