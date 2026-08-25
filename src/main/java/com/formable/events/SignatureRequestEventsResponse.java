package com.formable.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record SignatureRequestEventsResponse(
    List<SignatureRequestEvent> signatureRequestEvents) {

  public record SignatureRequestEvent(EventMetadata event, SigningPayload signing) {}

  public record EventMetadata(
      @JsonProperty("event_id") String eventId,
      @JsonProperty("event_category") String eventCategory,
      @JsonProperty("event_type") String eventType,
      @JsonProperty("event_time") long eventTime) {}

  public record SigningPayload(
      @JsonProperty("signature_request_id") String signatureRequestId,
      @JsonProperty("recipient_signature_id") String recipientSignatureId) {}
}
