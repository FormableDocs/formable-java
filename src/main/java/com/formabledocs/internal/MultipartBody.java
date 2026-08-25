package com.formabledocs.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class MultipartBody {
  private final String boundary;
  private final byte[] content;

  private MultipartBody(String boundary, byte[] content) {
    this.boundary = boundary;
    this.content = content;
  }

  String contentType() {
    return "multipart/form-data; boundary=" + boundary;
  }

  byte[] content() {
    return content;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final String boundary = "----FormableFormBoundary" + UUID.randomUUID().toString().replace("-", "");
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public Builder addField(String name, String value) {
      writeHeaders(name, null);
      write("\r\n");
      write(value);
      write("\r\n");
      return this;
    }

    public Builder addFile(String name, String filename, byte[] bytes) {
      writeHeaders(name, filename);
      write("Content-Type: application/octet-stream\r\n\r\n");
      write(bytes);
      write("\r\n");
      return this;
    }

    public MultipartBody build() {
      write("--" + boundary + "--\r\n");
      return new MultipartBody(boundary, out.toByteArray());
    }

    private void writeHeaders(String name, String filename) {
      write("--" + boundary + "\r\n");
      write("Content-Disposition: form-data; name=\"" + name + "\"");
      if (filename != null) {
        write("; filename=\"" + filename + "\"");
      }
      write("\r\n");
    }

    private void write(String text) {
      write(text.getBytes(StandardCharsets.UTF_8));
    }

    private void write(byte[] bytes) {
      try {
        out.write(bytes);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
