package com.formabledocs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

final class MockApi implements AutoCloseable {
  record Captured(
      String method, String path, String query, String authorization, String contentType, byte[] body) {}

  record Canned(int status, String json) {}

  private final HttpServer server;
  private final List<Captured> captured = new ArrayList<>();
  private final ConcurrentLinkedQueue<Canned> responses = new ConcurrentLinkedQueue<>();

  MockApi() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/",
        exchange -> {
          byte[] body = exchange.getRequestBody().readAllBytes();
          synchronized (captured) {
            captured.add(
                new Captured(
                    exchange.getRequestMethod(),
                    exchange.getRequestURI().getRawPath(),
                    exchange.getRequestURI().getQuery() == null
                        ? ""
                        : exchange.getRequestURI().getQuery(),
                    header(exchange, "Authorization"),
                    header(exchange, "Content-Type"),
                    body));
          }
          Canned canned = responses.poll();
          if (canned == null) {
            canned = new Canned(500, "{\"error\":\"no mock response queued\"}");
          }
          byte[] response = canned.json.getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(canned.status, response.length);
          try (OutputStream out = exchange.getResponseBody()) {
            out.write(response);
          }
        });
    server.start();
  }

  void enqueue(Object body) {
    enqueue(200, body);
  }

  void enqueue(int status, Object body) {
    try {
      responses.add(new Canned(status, JsonBytes.write(body)));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  void enqueueJson(String json) {
    responses.add(new Canned(200, json));
  }

  Formable client() {
    return Formable.builder().apiKey("test-key").baseUrl(baseUrl()).build();
  }

  String baseUrl() {
    return "http://127.0.0.1:" + server.getAddress().getPort() + "/v1";
  }

  Captured last() {
    synchronized (captured) {
      return captured.get(captured.size() - 1);
    }
  }

  Captured get(int index) {
    synchronized (captured) {
      return captured.get(index);
    }
  }

  String lastBody() {
    return new String(last().body(), StandardCharsets.UTF_8);
  }

  @Override
  public void close() {
    server.stop(0);
  }

  private static String header(HttpExchange exchange, String name) {
    List<String> values = exchange.getRequestHeaders().get(name);
    return values == null || values.isEmpty() ? "" : values.get(0);
  }

  private static final class JsonBytes {
    private static final com.fasterxml.jackson.databind.ObjectMapper MAPPER =
        new com.fasterxml.jackson.databind.ObjectMapper();

    static String write(Object body) throws Exception {
      if (body instanceof String string) {
        return string;
      }
      return MAPPER.writeValueAsString(body);
    }
  }
}
