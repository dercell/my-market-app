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
 * ChargeStatus
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-06T15:12:08.905819+03:00[Europe/Moscow]", comments = "Generator version: 7.22.0")
public class ChargeStatus {

  private String status;

  private Boolean isSuccess;

  public ChargeStatus() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ChargeStatus(String status, Boolean isSuccess) {
    this.status = status;
    this.isSuccess = isSuccess;
  }

  public ChargeStatus status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
   */
  @NotNull 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.status = status;
  }

  public ChargeStatus isSuccess(Boolean isSuccess) {
    this.isSuccess = isSuccess;
    return this;
  }

  /**
   * Get isSuccess
   * @return isSuccess
   */
  @NotNull 
  @Schema(name = "isSuccess", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("isSuccess")
  public Boolean getIsSuccess() {
    return isSuccess;
  }

  @JsonProperty("isSuccess")
  public void setIsSuccess(Boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChargeStatus chargeStatus = (ChargeStatus) o;
    return Objects.equals(this.status, chargeStatus.status) &&
        Objects.equals(this.isSuccess, chargeStatus.isSuccess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, isSuccess);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChargeStatus {\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    isSuccess: ").append(toIndentedString(isSuccess)).append("\n");
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

