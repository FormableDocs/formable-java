# formable-java

Official Java SDK for the [Formable API](https://api.formabledocs.com) (v1). Covers templates, signature requests, redlining, and billing.

- Typed request and response models (Jackson)
- Uses the JDK `HttpClient` (Java 17+)
- One runtime dependency: Jackson Databind

## Installation

Maven:

```xml
<dependency>
  <groupId>com.formable</groupId>
  <artifactId>formable-sdk</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("com.formable:formable-sdk:0.1.0")
```

## Usage

```java
import com.formable.Formable;

Formable formable = new Formable(System.getenv("FORMABLE_API_KEY"));
```

### Templates

```java
import com.formable.model.TemplateSignerRole;
import java.nio.file.Path;
import java.util.List;

var created = formable.templates.create(
    Path.of("nda.docx"),
    List.of(
        new TemplateSignerRole("Client", 0),
        new TemplateSignerRole("Witness", 1)
    )
);

String templateId = created.templateId();

// Mint a fresh edit URL later (expires after 1 day)
var edit = formable.templates.createEditUrl(templateId);
```

### Signature requests

```java
import com.formable.model.CreateSignatureRequest;
import com.formable.model.Signer;
import java.time.Instant;

// Formable emails each signer a signing link
var request = formable.signatureRequests.create(
    CreateSignatureRequest.builder()
        .templateId(templateId)
        .addSigner(new Signer("jane@example.com", "Jane Doe", "Client"))
        .addSigner(new Signer("bob@example.com", "Bob Smith", "Witness"))
        .build()
);

// Embedded flow: mint signing URLs to embed in an iframe yourself
var embedded = formable.signatureRequests.createEmbedded(
    CreateSignatureRequest.builder()
        .templateId(templateId)
        .addSigner(new Signer("jane@example.com", "Jane Doe", "Client"))
        .testMode(true)
        .build()
);

var signer = embedded.signers().get(0);
var signing = formable.signatureRequests.createSigningUrl(signer.recipientSignatureId());

// Track progress
var current = formable.signatureRequests.get(embedded.signatureRequestId());
var all = formable.signatureRequests.list(Instant.parse("2026-01-01T00:00:00Z"));
var events = formable.signatureRequests.getEvents(embedded.signatureRequestId());

// Download the signed document once completed
var envelope = formable.signatureRequests.getSignedEnvelope(embedded.signatureRequestId());
```

### Redline requests

```java
import com.formable.model.CreateRedlineRequest;
import com.formable.model.RedlineMember;
import com.formable.model.RedlineMemberRole;
import com.formable.model.RedlineRequestMetadata;
import java.util.List;

var created = formable.redlineRequests.create(
    CreateRedlineRequest.builder()
        .templateId(templateId)
        .addMember(new RedlineMember("us@example.com", "John Doe", RedlineMemberRole.DISCLOSING_PARTY))
        .addMember(new RedlineMember("them@example.com", "Jane Smith", RedlineMemberRole.RECEIVING_PARTY))
        .metadata(new RedlineRequestMetadata("Mutual NDA"))
        .build()
);

String redlineRequestId = created.redlineRequestId();

// Mint a redline URL for a member (embed in an iframe)
var url = formable.redlineRequests.createUrl(redlineRequestId, "them@example.com");

// Manage members and track progress
formable.redlineRequests.updateMembers(
    redlineRequestId,
    List.of(new RedlineMember("counsel@example.com", "Counsel", RedlineMemberRole.RECEIVING_COUNSEL))
);
var redline = formable.redlineRequests.get(redlineRequestId);
var events = formable.redlineRequests.getEvents(redlineRequestId);
```

### Billing and health

```java
var billing = formable.billing();
int sessions = billing.numberOfRedliningSessions();

var health = formable.health();
```

## Error handling

All non-2xx responses throw a `FormableException` with the server's error message, HTTP status, and parsed response body.

```java
import com.formable.FormableException;

try {
    formable.signatureRequests.get("missing-id");
} catch (FormableException error) {
    System.err.println(error.status() + " " + error.getMessage());
}
```

## Configuration

```java
import java.net.http.HttpClient;
import java.time.Duration;

Formable formable = Formable.builder()
    .apiKey(System.getenv("FORMABLE_API_KEY"))
    .baseUrl("https://api.formabledocs.com/v1")
    .timeout(Duration.ofSeconds(60))
    .httpClient(HttpClient.newHttpClient())
    .build();
```

| Option       | Description                                               | Default                           |
| ------------ | --------------------------------------------------------- | --------------------------------- |
| `apiKey`     | Your Formable API key (sent as a bearer token). Required. | -                                 |
| `baseUrl`    | Override the API base URL.                                | `https://api.formabledocs.com/v1` |
| `timeout`    | Per-request timeout.                                      | 60 seconds                        |
| `httpClient` | Custom `java.net.http.HttpClient`.                        | JDK client with 60s connect timeout |

## Development

```bash
./mvnw test
```
