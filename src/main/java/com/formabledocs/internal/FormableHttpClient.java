package com.formabledocs.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.formabledocs.FormableException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

public final class FormableHttpClient {
  public static final String DEFAULT_BASE_URL = "https://api.formabledocs.com/v1";
  public static final String VERSION = "0.1.0";

  private final String apiKey;
  private final String baseUrl;
  private final HttpClient http;
  private final Duration timeout;

  public FormableHttpClient(ClientOptions options) {
    if (options.apiKey == null || options.apiKey.isBlank()) {
      throw new IllegalArgumentException("Formable API key is required");
    }
    this.apiKey = options.apiKey;
    String resolved = options.baseUrl == null || options.baseUrl.isBlank()
        ? DEFAULT_BASE_URL
        : options.baseUrl;
    this.baseUrl = stripTrailingSlash(resolved);
    this.timeout = options.timeout == null ? Duration.ofSeconds(60) : options.timeout;
    this.http = options.httpClient == null
        ? HttpClient.newBuilder().connectTimeout(timeout).build()
        : options.httpClient;
  }

  public <T> T get(String path, Class<T> type) {
    return get(path, Map.of(), type);
  }

  public <T> T get(String path, Map<String, String> query, Class<T> type) {
    return send("GET", path, query, null, null, type, null);
  }

  public <T> T get(String path, Map<String, String> query, TypeReference<T> type) {
    return send("GET", path, query, null, null, null, type);
  }

  public <T> T post(String path, Class<T> type) {
    return post(path, null, type);
  }

  public <T> T post(String path, Object body, Class<T> type) {
    return send("POST", path, Map.of(), body, null, type, null);
  }

  public <T> T put(String path, Object body, Class<T> type) {
    return send("PUT", path, Map.of(), body, null, type, null);
  }

  public <T> T postForm(String path, MultipartBody form, Class<T> type) {
    return send("POST", path, Map.of(), null, form, type, null);
  }

  private <T> T send(
      String method,
      String path,
      Map<String, String> query,
      Object jsonBody,
      MultipartBody form,
      Class<T> classType,
      TypeReference<T> refType) {
    HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path, query))
        .timeout(timeout)
        .header("Authorization", "Bearer " + apiKey)
        .header("Accept", "application/json")
        .header("User-Agent", "formable-java/" + VERSION);

    if (form != null) {
      builder.header("Content-Type", form.contentType());
      builder.method(method, HttpRequest.BodyPublishers.ofByteArray(form.content()));
    } else if (jsonBody != null) {
      builder.header("Content-Type", "application/json");
      builder.method(method, HttpRequest.BodyPublishers.ofString(Json.write(jsonBody)));
    } else {
      builder.method(method, HttpRequest.BodyPublishers.noBody());
    }

    HttpResponse<String> response;
    try {
      response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      throw new UncheckedIOException("Formable API request failed", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Formable API request interrupted", e);
    }

    String text = response.body();
    int status = response.statusCode();
    if (status < 200 || status >= 300) {
      Object parsed = Json.parse(text);
      String message = errorMessage(parsed, status);
      throw new FormableException(message, status, parsed);
    }
    if (text == null || text.isBlank()) {
      return null;
    }
    return classType != null ? Json.read(text, classType) : Json.read(text, refType);
  }

  private URI uri(String path, Map<String, String> query) {
    StringBuilder url = new StringBuilder(baseUrl).append(path);
    boolean first = true;
    for (Map.Entry<String, String> entry : query.entrySet()) {
      if (entry.getValue() == null) {
        continue;
      }
      url.append(first ? '?' : '&');
      first = false;
      url.append(encodeQuery(entry.getKey())).append('=').append(encodeQuery(entry.getValue()));
    }
    return URI.create(url.toString());
  }

  private static String errorMessage(Object parsed, int status) {
    if (parsed instanceof Map<?, ?> map) {
      Object error = map.get("error");
      if (error instanceof String message && !message.isBlank()) {
        return message;
      }
    }
    return "Request failed with status " + status;
  }

  private static String encodeQuery(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  private static String stripTrailingSlash(String value) {
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }
}
