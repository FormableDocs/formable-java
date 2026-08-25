package com.formable.resources;

import com.formable.internal.FormableHttpClient;
import com.formable.internal.Json;
import com.formable.internal.MultipartBody;
import com.formable.internal.Paths;
import com.formable.model.CreateTemplateResponse;
import com.formable.model.TemplateEditUrlResponse;
import com.formable.model.TemplateSignerRole;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Templates {
  private final FormableHttpClient http;

  public Templates(FormableHttpClient http) {
    this.http = http;
  }

  public CreateTemplateResponse create(Path file) {
    return create(file, null);
  }

  public CreateTemplateResponse create(Path file, List<TemplateSignerRole> signerRoles) {
    try {
      return create(Files.readAllBytes(file), file.getFileName().toString(), signerRoles);
    } catch (IOException e) {
      throw new UncheckedIOException("Unable to read template file", e);
    }
  }

  public CreateTemplateResponse create(byte[] file, String filename) {
    return create(file, filename, null);
  }

  public CreateTemplateResponse create(
      byte[] file, String filename, List<TemplateSignerRole> signerRoles) {
    MultipartBody.Builder form = MultipartBody.builder()
        .addFile("file", filename, file)
        .addField("filename", filename);
    if (signerRoles != null) {
      form.addField("signer_roles", Json.write(signerRoles));
    }
    return http.postForm(Paths.templates(), form.build(), CreateTemplateResponse.class);
  }

  public TemplateEditUrlResponse createEditUrl(String templateId) {
    return http.post(Paths.templateEditUrl(templateId), TemplateEditUrlResponse.class);
  }
}
