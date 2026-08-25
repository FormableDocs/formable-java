package com.formable.internal;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Paths {
  private Paths() {}

  public static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
  }

  public static Map<String, String> updatedSince(String updatedSince) {
    if (updatedSince == null) {
      return Map.of();
    }
    Map<String, String> query = new LinkedHashMap<>();
    query.put("updatedSince", updatedSince);
    return query;
  }

  public static String templates() {
    return "/templates";
  }

  public static String templateEditUrl(String templateId) {
    return "/templates/" + encode(templateId) + "/edit-url";
  }

  public static String signatureRequests() {
    return "/signature-requests";
  }

  public static String signatureRequest(String signatureRequestId) {
    return "/signature-requests/" + encode(signatureRequestId);
  }

  public static String signatureRequestEvents(String signatureRequestId) {
    return signatureRequest(signatureRequestId) + "/events";
  }

  public static String signedEnvelope(String signatureRequestId) {
    return signatureRequest(signatureRequestId) + "/signed-envelope";
  }

  public static String embeddedSignatureRequests() {
    return "/signature-requests/embedded";
  }

  public static String signingUrl(String recipientSignatureId) {
    return "/recipient-signatures/" + encode(recipientSignatureId) + "/url";
  }

  public static String redlineRequests() {
    return "/redline-requests";
  }

  public static String redlineRequest(String redlineRequestId) {
    return "/redline-requests/" + encode(redlineRequestId);
  }

  public static String redlineMembers(String redlineRequestId) {
    return redlineRequest(redlineRequestId) + "/members";
  }

  public static String redlineUrl(String redlineRequestId) {
    return redlineRequest(redlineRequestId) + "/url";
  }

  public static String redlineEvents(String redlineRequestId) {
    return redlineRequest(redlineRequestId) + "/events";
  }
}
