package com.formabledocs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.formabledocs.model.CreateRedlineRequest;
import com.formabledocs.model.CreateSignatureRequest;
import com.formabledocs.model.FieldValue;
import com.formabledocs.model.RedlineMember;
import com.formabledocs.model.RedlineMemberRole;
import com.formabledocs.model.RedlineRequestMetadata;
import com.formabledocs.model.Signer;
import com.formabledocs.model.TemplateSignerRole;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FormableTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void requiresApiKey() {
    assertThrows(IllegalArgumentException.class, () -> new Formable(""));
    assertThrows(IllegalArgumentException.class, () -> Formable.builder().apiKey("  ").build());
  }

  @Test
  void sendsBearerTokenAndBaseUrl() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("status", "healthy"));
      var health = api.client().health();
      assertEquals("healthy", health.status());
      assertEquals("Bearer test-key", api.last().authorization());
      assertEquals("/v1/health", api.last().path());
    }
  }

  @Test
  void errorRaisesFormableException() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(404, Map.of("error", "Template not found for id abc"));
      FormableException error =
          assertThrows(
              FormableException.class, () -> api.client().signatureRequests.get("abc"));
      assertEquals(404, error.status());
      assertEquals("Template not found for id abc", error.getMessage());
      assertEquals(Map.of("error", "Template not found for id abc"), error.body());
    }
  }

  @Test
  void createTemplateMultipart() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("templateId", "tmpl_1"));
      var result =
          api.client()
              .templates
              .create(
                  "file-bytes".getBytes(),
                  "nda.docx",
                  List.of(new TemplateSignerRole("Client", 0)));
      assertEquals("tmpl_1", result.templateId());
      assertEquals("/v1/templates", api.last().path());
      assertTrue(api.last().contentType().startsWith("multipart/form-data"));
      String body = api.lastBody();
      assertTrue(body.contains("name=\"filename\""));
      assertTrue(body.contains("nda.docx"));
      assertTrue(body.contains("[{\"name\":\"Client\",\"order\":0}]"));
    }
  }

  @Test
  void createSignatureRequestBody() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("signatureRequestId", "sr_1"));
      api.client()
          .signatureRequests
          .create(
              CreateSignatureRequest.builder()
                  .templateId("tmpl_1")
                  .addSigner(new Signer("jane@example.com", "Jane", "Client"))
                  .testMode(true)
                  .fields(List.of(new FieldValue("field_1", "hello")))
                  .build());
      assertEquals(
          Map.of(
              "templateId", "tmpl_1",
              "signers",
                  List.of(
                      Map.of("email", "jane@example.com", "name", "Jane", "role", "Client")),
              "testMode", true,
              "fields", List.of(Map.of("fieldId", "field_1", "value", "hello"))),
          json(api.lastBody()));
    }
  }

  @Test
  void createEmbeddedSignatureRequest() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("signatureRequestId", "sr_1"));
      api.client()
          .signatureRequests
          .createEmbedded(
              CreateSignatureRequest.builder()
                  .templateId("tmpl_1")
                  .addSigner(new Signer("jane@example.com", "Jane"))
                  .build());
      assertEquals("/v1/signature-requests/embedded", api.last().path());
      assertEquals(
          Map.of(
              "templateId", "tmpl_1",
              "signers", List.of(Map.of("email", "jane@example.com", "name", "Jane"))),
          json(api.lastBody()));
    }
  }

  @Test
  void listWithUpdatedSinceInstant() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(List.of());
      api.client()
          .signatureRequests
          .list(Instant.parse("2026-01-01T00:00:00Z"));
      assertEquals("updatedSince=2026-01-01T00:00:00Z", api.last().query());
    }
  }

  @Test
  void listWithoutUpdatedSinceOmitsParam() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(List.of());
      api.client().redlineRequests.list();
      assertEquals("", api.last().query());
    }
  }

  @Test
  void createRedlineRequestBody() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("redlineRequestId", "rr_1", "templateId", "tmpl_2"));
      api.client()
          .redlineRequests
          .create(
              CreateRedlineRequest.builder()
                  .templateId("tmpl_1")
                  .addMember(
                      new RedlineMember(
                          "a@example.com", "A", RedlineMemberRole.DISCLOSING_PARTY))
                  .metadata(new RedlineRequestMetadata("Mutual NDA"))
                  .build());
      assertEquals(
          Map.of(
              "templateId", "tmpl_1",
              "members",
                  List.of(
                      Map.of(
                          "email", "a@example.com",
                          "displayName", "A",
                          "role", "DisclosingParty")),
              "metadata", Map.of("subject", "Mutual NDA")),
          json(api.lastBody()));
    }
  }

  @Test
  void pathParamsAreEncoded() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("members", List.of()));
      api.client()
          .redlineRequests
          .updateMembers(
              "id/with slash",
              List.of(
                  new RedlineMember("a@b.com", "A", RedlineMemberRole.RECEIVING_PARTY)));
      assertEquals("/v1/redline-requests/id%2Fwith%20slash/members", api.last().path());
      assertEquals("PUT", api.last().method());
    }
  }

  @Test
  void createSigningUrlPath() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("signingUrl", "https://example.com", "expiresAt", "soon"));
      api.client().signatureRequests.createSigningUrl("rsig_1");
      assertEquals("/v1/recipient-signatures/rsig_1/url", api.last().path());
      assertEquals("POST", api.last().method());
    }
  }

  @Test
  void createRedlineUrlBody() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("redlineUrl", "https://example.com", "expiresAt", "soon"));
      api.client().redlineRequests.createUrl("rr_1", "member@example.com");
      assertEquals("/v1/redline-requests/rr_1/url", api.last().path());
      assertEquals(Map.of("memberEmail", "member@example.com"), json(api.lastBody()));
    }
  }

  @Test
  void getSignedEnvelopePath() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("signedEnvelopePresignedUrl", "https://s3.example.com"));
      api.client().signatureRequests.getSignedEnvelope("sr_1");
      assertEquals("/v1/signature-requests/sr_1/signed-envelope", api.last().path());
    }
  }

  @Test
  void getEventsPaths() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("signatureRequestEvents", List.of()));
      api.enqueueJson("{\"redlineRequestEvents\":[]}");
      Formable formable = api.client();
      formable.signatureRequests.getEvents("sr_1");
      formable.redlineRequests.getEvents("rr_1");
      assertEquals("/v1/signature-requests/sr_1/events", api.get(0).path());
      assertEquals("/v1/redline-requests/rr_1/events", api.get(1).path());
    }
  }

  @Test
  void billing() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(Map.of("numberOfRedliningSessions", 42));
      assertEquals(42, api.client().billing().numberOfRedliningSessions());
    }
  }

  @Test
  void createEditUrlPath() throws Exception {
    try (MockApi api = new MockApi()) {
      api.enqueue(
          Map.of(
              "editUrl", "https://app.formabledocs.com/template-setup/tmpl_1",
              "expiresAt", "2026-01-16T10:30:00.000Z"));
      api.client().templates.createEditUrl("tmpl_1");
      assertEquals("/v1/templates/tmpl_1/edit-url", api.last().path());
      assertEquals("POST", api.last().method());
    }
  }

  private static Map<String, Object> json(String body) throws Exception {
    return MAPPER.readValue(body, new TypeReference<>() {});
  }
}
