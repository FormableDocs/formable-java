package com.formable.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SignatureRequestStatus {
  CREATED("Created"),
  COMPLETED("Completed"),
  EXPIRED("Expired"),
  @JsonEnumDefaultValue
  UNKNOWN("Unknown");

  private final String value;

  SignatureRequestStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
