package com.formable.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.formable.events.RedlineRequestEventsResponse;
import com.formable.internal.FormableHttpClient;
import com.formable.internal.Paths;
import com.formable.model.CreateRedlineRequest;
import com.formable.model.CreateRedlineRequestResponse;
import com.formable.model.RedlineMember;
import com.formable.model.RedlineMembersResponse;
import com.formable.model.RedlineRequest;
import com.formable.model.RedlineUrlResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class RedlineRequests {
  private final FormableHttpClient http;

  public RedlineRequests(FormableHttpClient http) {
    this.http = http;
  }

  public CreateRedlineRequestResponse create(CreateRedlineRequest params) {
    return http.post(Paths.redlineRequests(), params, CreateRedlineRequestResponse.class);
  }

  public List<RedlineRequest> list() {
    return list((String) null);
  }

  public List<RedlineRequest> list(Instant updatedSince) {
    return list(updatedSince == null ? null : updatedSince.toString());
  }

  public List<RedlineRequest> list(String updatedSince) {
    return http.get(
        Paths.redlineRequests(),
        Paths.updatedSince(updatedSince),
        new TypeReference<List<RedlineRequest>>() {});
  }

  public RedlineRequest get(String redlineRequestId) {
    return http.get(Paths.redlineRequest(redlineRequestId), RedlineRequest.class);
  }

  public RedlineMembersResponse updateMembers(String redlineRequestId, List<RedlineMember> members) {
    return http.put(
        Paths.redlineMembers(redlineRequestId),
        Map.of("members", members),
        RedlineMembersResponse.class);
  }

  public RedlineUrlResponse createUrl(String redlineRequestId, String memberEmail) {
    return http.post(
        Paths.redlineUrl(redlineRequestId),
        Map.of("memberEmail", memberEmail),
        RedlineUrlResponse.class);
  }

  public RedlineRequestEventsResponse getEvents(String redlineRequestId) {
    return http.get(Paths.redlineEvents(redlineRequestId), RedlineRequestEventsResponse.class);
  }
}
