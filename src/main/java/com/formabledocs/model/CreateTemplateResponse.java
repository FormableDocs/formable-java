package com.formabledocs.model;

public record CreateTemplateResponse(
    String templateId, TemplateEditUrlResponse editTemplateAccess) {}
