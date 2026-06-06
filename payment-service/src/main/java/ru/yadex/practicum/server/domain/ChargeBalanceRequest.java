package ru.yadex.practicum.server.domain;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ChargeBalanceRequest
 */

@JsonTypeName("chargeBalance_request")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-06T15:12:08.905819+03:00[Europe/Moscow]", comments = "Generator version: 7.22.0")
public class ChargeBalanceRequest {

  private Long totalSum;

  public ChargeBalanceRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChargeBalanceRequest(Long totalSum) {
    this.totalSum = totalSum;
  }

  public ChargeBalanceRequest totalSum(Long totalSum) {
    this.totalSum = totalSum;
    return this;
  }

  /**
   * Get totalSum
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChargeBalanceRequest chargeBalanceRequest = (ChargeBalanceRequest) o;
    return Objects.equals(this.totalSum, chargeBalanceRequest.totalSum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalSum);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChargeBalanceRequest {\n");
    sb.append("    totalSum: ").append(toIndentedString(totalSum)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    return o == null ? "null" : o.toString().replace("\n", "\n    ");
  }
}

