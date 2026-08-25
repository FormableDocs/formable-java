package com.formabledocs.internal;

import java.net.http.HttpClient;
import java.time.Duration;

public final class ClientOptions {
  public final String apiKey;
  public final String baseUrl;
  public final HttpClient httpClient;
  public final Duration timeout;

  public ClientOptions(String apiKey, String baseUrl, HttpClient httpClient, Duration timeout) {
    this.apiKey = apiKey;
    this.baseUrl = baseUrl;
    this.httpClient = httpClient;
    this.timeout = timeout;
  }
}
