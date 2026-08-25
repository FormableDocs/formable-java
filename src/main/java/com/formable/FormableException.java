package com.formable;

/**
 * Thrown for any non-2xx response from the Formable API.
 */
public class FormableException extends RuntimeException {
  private final int status;
  private final Object body;

  public FormableException(String message, int status, Object body) {
    super(message);
    this.status = status;
    this.body = body;
  }

  public int status() {
    return status;
  }

  public Object body() {
    return body;
  }
}
