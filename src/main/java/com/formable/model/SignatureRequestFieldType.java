package com.formable.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SignatureRequestFieldType {
  TEXT("text"),
  PARAGRAPH("paragraph"),
  CHECKBOX("checkbox"),
  DATE("date"),
  AMOUNT("amount"),
  DROPDOWN("dropdown"),
  SIGNATURE("signature"),
  @JsonEnumDefaultValue
  UNKNOWN("unknown");

  private final String value;

  SignatureRequestFieldType(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
