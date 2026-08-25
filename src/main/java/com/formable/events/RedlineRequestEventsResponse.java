package com.formable.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RedlineRequestEventsResponse(List<RedlineRequestEvent> redlineRequestEvents) {

  public record RedlineRequestEvent(EventMetadata event, RedliningPayload redlining) {}

  public record EventMetadata(
      @JsonProperty("event_id") String eventId,
      @JsonProperty("event_category") String eventCategory,
      @JsonProperty("event_type") String eventType,
      @JsonProperty("event_time") long eventTime) {}

  public record RedliningPayload(
      @JsonProperty("redline_request_id") String redlineRequestId,
      @JsonProperty("redline_member_role") String redlineMemberRole,
      @JsonProperty("redline_edit_insertion") String redlineEditInsertion,
      @JsonProperty("redline_edit_deletion") String redlineEditDeletion,
      String content,
      @JsonProperty("change_type") String changeType,
      @JsonProperty("comment_added") String commentAdded,
      String message,
      @JsonProperty("author_email") String authorEmail) {}
}
