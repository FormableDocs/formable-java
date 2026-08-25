package com.formable.model;

import java.util.List;

public record SignatureRequest(
    String signatureRequestId,
    String templateId,
    List<SignatureRequestSigner> signers,
    Party sender,
    SignatureRequestStatus status,
    boolean testMode,
    List<SignatureRequestField> fields) {}
