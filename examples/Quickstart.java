import com.formabledocs.Formable;
import com.formabledocs.FormableException;
import com.formabledocs.model.CreateSignatureRequest;
import com.formabledocs.model.Signer;
import com.formabledocs.model.TemplateSignerRole;
import java.nio.file.Path;
import java.util.List;

/**
 * Usage: FORMABLE_API_KEY=... java examples/Quickstart.java [agreement.pdf]
 *
 * Run from the repo after {@code mvn package}, with the SDK jar and Jackson on the classpath.
 */
public final class Quickstart {
  public static void main(String[] args) throws Exception {
    String apiKey = System.getenv("FORMABLE_API_KEY");
    if (apiKey == null || apiKey.isBlank()) {
      System.err.println("Set FORMABLE_API_KEY (from https://app.formabledocs.com/settings).");
      System.exit(1);
    }

    String baseUrl = System.getenv("FORMABLE_BASE_URL");
    Formable formable = baseUrl == null
        ? new Formable(apiKey)
        : Formable.builder().apiKey(apiKey).baseUrl(baseUrl).build();

    try {
      dump("health", formable.health());
      dump("billing", formable.billing());
      dump("signature requests", formable.signatureRequests.list());
      dump("redline requests", formable.redlineRequests.list());

      if (args.length == 0) {
        System.err.println("Pass a PDF, DOC, or DOCX to upload a template and create a test signature request:");
        System.err.println("  java examples/Quickstart.java ./agreement.pdf");
        return;
      }

      Path file = Path.of(args[0]);
      var template = formable.templates.create(
          file, List.of(new TemplateSignerRole("Client", 0)));
      dump("template", template);

      var edit = formable.templates.createEditUrl(template.templateId());
      dump("edit url", edit);

      String signerEmail = System.getenv().getOrDefault("FORMABLE_SIGNER_EMAIL", "signer@example.com");
      var embedded = formable.signatureRequests.createEmbedded(
          CreateSignatureRequest.builder()
              .templateId(template.templateId())
              .addSigner(new Signer(signerEmail, "Jane Doe", "Client"))
              .testMode(true)
              .build());
      dump("embedded signature request", embedded);

      var signer = embedded.signers().get(0);
      dump("signing url", formable.signatureRequests.createSigningUrl(signer.recipientSignatureId()));
      dump("signature request", formable.signatureRequests.get(embedded.signatureRequestId()));
    } catch (FormableException error) {
      System.err.println(error.status() + " " + error.getMessage());
      System.exit(1);
    }
  }

  private static void dump(String label, Object value) {
    System.out.println("=== " + label + " ===");
    System.out.println(value);
    System.out.println();
  }
}
