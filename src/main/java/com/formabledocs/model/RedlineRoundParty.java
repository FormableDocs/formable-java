package com.formabledocs.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RedlineRoundParty {
  DISCLOSING("Disclosing"),
  RECEIVING("Receiving"),
  @JsonEnumDefaultValue
  UNKNOWN("Unknown");

  private final String value;

  RedlineRoundParty(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
