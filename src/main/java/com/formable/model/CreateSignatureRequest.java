package com.formable.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record CreateSignatureRequest(
    String templateId,
    List<Signer> signers,
    Party sender,
    Boolean testMode,
    List<FieldValue> fields) {

  public CreateSignatureRequest {
    Objects.requireNonNull(templateId, "templateId is required");
    Objects.requireNonNull(signers, "signers is required");
    signers = List.copyOf(signers);
    if (fields != null) {
      fields = List.copyOf(fields);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String templateId;
    private final List<Signer> signers = new ArrayList<>();
    private Party sender;
    private Boolean testMode;
    private List<FieldValue> fields;

    public Builder templateId(String templateId) {
      this.templateId = templateId;
      return this;
    }

    public Builder signers(List<Signer> signers) {
      this.signers.clear();
      this.signers.addAll(signers);
      return this;
    }

    public Builder addSigner(Signer signer) {
      this.signers.add(signer);
      return this;
    }

    public Builder sender(Party sender) {
      this.sender = sender;
      return this;
    }

    public Builder testMode(boolean testMode) {
      this.testMode = testMode;
      return this;
    }

    public Builder fields(List<FieldValue> fields) {
      this.fields = fields;
      return this;
    }

    public CreateSignatureRequest build() {
      return new CreateSignatureRequest(templateId, signers, sender, testMode, fields);
    }
  }
}
