package com.formable.model;

public record SignatureRequestField(
    String fieldId,
    SignatureRequestFieldType type,
    boolean required,
    boolean filled,
    String recipientSignatureId,
    Object value,
    String label,
    String role,
    String unit,
    String signedAt) {}
