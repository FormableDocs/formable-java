package com.formable.model;

public record SignatureRequestListItem(
    String signatureRequestId,
    String templateId,
    Party signer,
    Party sender,
    SignatureRequestStatus status,
    boolean testMode) {}
