package com.formable.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RedlineRequestStatus {
  DISCLOSING_PARTY_DRAFT("DisclosingPartyDraft"),
  DISCLOSING_PARTY_REQUESTED_REVIEW("DisclosingPartyRequestedReview"),
  DOCUMENT_READY_FOR_SIGNING("DocumentReadyForSigning"),
  RECEIVING_PARTY_DRAFT("ReceivingPartyDraft"),
  RECEIVING_PARTY_OPENED("ReceivingPartyOpened"),
  RECEIVING_PARTY_REQUESTED_REVIEW("ReceivingPartyRequestedReview"),
  @JsonEnumDefaultValue
  UNKNOWN("Unknown");

  private final String value;

  RedlineRequestStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String value() {
    return value;
  }
}
