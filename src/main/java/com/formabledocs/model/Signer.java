package com.formabledocs.model;

public record Signer(String email, String name, String role) {
  public Signer(String email, String name) {
    this(email, name, null);
  }
}
