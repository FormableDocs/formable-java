package com.formable;

import com.formable.internal.ClientOptions;
import com.formable.internal.FormableHttpClient;
import com.formable.model.BillingResponse;
import com.formable.model.HealthResponse;
import com.formable.resources.RedlineRequests;
import com.formable.resources.SignatureRequests;
import com.formable.resources.Templates;
import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Official Java client for the Formable API (v1).
 */
public final class Formable {
  public static final String DEFAULT_BASE_URL = FormableHttpClient.DEFAULT_BASE_URL;
  public static final String VERSION = FormableHttpClient.VERSION;

  public final Templates templates;
  public final SignatureRequests signatureRequests;
  public final RedlineRequests redlineRequests;
  private final FormableHttpClient http;

  public Formable(String apiKey) {
    this(builder().apiKey(apiKey));
  }

  private Formable(Builder builder) {
    this.http = new FormableHttpClient(
        new ClientOptions(builder.apiKey, builder.baseUrl, builder.httpClient, builder.timeout));
    this.templates = new Templates(http);
    this.signatureRequests = new SignatureRequests(http);
    this.redlineRequests = new RedlineRequests(http);
  }

  public BillingResponse billing() {
    return http.get("/billing", BillingResponse.class);
  }

  public HealthResponse health() {
    return http.get("/health", HealthResponse.class);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    private HttpClient httpClient;
    private Duration timeout = Duration.ofSeconds(60);

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder httpClient(HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    public Builder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public Formable build() {
      return new Formable(this);
    }
  }
}
