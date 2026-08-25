package com.formabledocs.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.formabledocs.events.SignatureRequestEventsResponse;
import com.formabledocs.internal.FormableHttpClient;
import com.formabledocs.internal.Paths;
import com.formabledocs.model.CreateSignatureRequest;
import com.formabledocs.model.SignatureRequest;
import com.formabledocs.model.SignatureRequestListItem;
import com.formabledocs.model.SignedEnvelopeResponse;
import com.formabledocs.model.SigningUrlResponse;
import java.time.Instant;
import java.util.List;

public final class SignatureRequests {
  private final FormableHttpClient http;

  public SignatureRequests(FormableHttpClient http) {
    this.http = http;
  }

  public SignatureRequest create(CreateSignatureRequest params) {
    return http.post(Paths.signatureRequests(), params, SignatureRequest.class);
  }

  public SignatureRequest createEmbedded(CreateSignatureRequest params) {
    return http.post(Paths.embeddedSignatureRequests(), params, SignatureRequest.class);
  }

  public List<SignatureRequestListItem> list() {
    return list((String) null);
  }

  public List<SignatureRequestListItem> list(Instant updatedSince) {
    return list(updatedSince == null ? null : updatedSince.toString());
  }

  public List<SignatureRequestListItem> list(String updatedSince) {
    return http.get(
        Paths.signatureRequests(),
        Paths.updatedSince(updatedSince),
        new TypeReference<List<SignatureRequestListItem>>() {});
  }

  public SignatureRequest get(String signatureRequestId) {
    return http.get(Paths.signatureRequest(signatureRequestId), SignatureRequest.class);
  }

  public SignatureRequestEventsResponse getEvents(String signatureRequestId) {
    return http.get(
        Paths.signatureRequestEvents(signatureRequestId), SignatureRequestEventsResponse.class);
  }

  public SignedEnvelopeResponse getSignedEnvelope(String signatureRequestId) {
    return http.get(Paths.signedEnvelope(signatureRequestId), SignedEnvelopeResponse.class);
  }

  public SigningUrlResponse createSigningUrl(String recipientSignatureId) {
    return http.post(Paths.signingUrl(recipientSignatureId), SigningUrlResponse.class);
  }
}
