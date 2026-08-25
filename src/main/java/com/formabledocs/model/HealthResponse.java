package com.formabledocs.model;

public record HealthResponse(String status, String timestamp, double uptime, String version) {}
