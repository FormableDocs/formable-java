package com.formabledocs.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RedlineMemberRole {
  DISCLOSING_PARTY("DisclosingParty"),
  RECEIVING_PARTY("ReceivingParty"),
  DISCLOSING_COUNSEL("DisclosingCounsel"),
  RECEIVING_COUNSEL("ReceivingCounsel"),
  @JsonEnumDefaultValue
  UNKNOWN("Unknown");

  private final String value;

  RedlineMemberRole(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
