package com.formable.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CreateRedlineRequest(
    String templateId,
    List<RedlineMember> members,
    Boolean testMode,
    RedlineRequestMetadata metadata) {

  public CreateRedlineRequest {
    Objects.requireNonNull(templateId, "templateId is required");
    Objects.requireNonNull(members, "members is required");
    members = List.copyOf(members);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String templateId;
    private final List<RedlineMember> members = new ArrayList<>();
    private Boolean testMode;
    private RedlineRequestMetadata metadata;

    public Builder templateId(String templateId) {
      this.templateId = templateId;
      return this;
    }

    public Builder members(List<RedlineMember> members) {
      this.members.clear();
      this.members.addAll(members);
      return this;
    }

    public Builder addMember(RedlineMember member) {
      this.members.add(member);
      return this;
    }

    public Builder testMode(boolean testMode) {
      this.testMode = testMode;
      return this;
    }

    public Builder metadata(RedlineRequestMetadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public CreateRedlineRequest build() {
      return new CreateRedlineRequest(templateId, members, testMode, metadata);
    }
  }
}
