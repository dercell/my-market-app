package ru.yadex.practicum.server.domain;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Balance
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-06T15:12:08.905819+03:00[Europe/Moscow]", comments = "Generator version: 7.22.0")
public class Balance {

  private Long balance;

  public Balance() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Balance(Long balance) {
    this.balance = balance;
  }

  public Balance balance(Long balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Get balance
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Balance balance = (Balance) o;
    return Objects.equals(this.balance, balance.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(balance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Balance {\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
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

