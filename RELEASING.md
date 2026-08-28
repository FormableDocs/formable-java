# Releasing

Maintainer guide for publishing `com.formabledocs:formable-sdk` to Maven Central. Consumers should follow the [README](README.md).

Use semver. The published version is `<version>` in `pom.xml`.

## One-time setup

Requires Java 17+, a GPG key, a verified `com.formabledocs` namespace, and a Central Portal token in `~/.m2/settings.xml`.

## Publish a version

1. Bump `<version>` in `pom.xml`.
2. Run tests:

   ```bash
   ./mvnw test
   ```

3. Commit the change on `main`.
4. Tag and push:

   ```bash
   git tag v0.1.0
   git push origin main v0.1.0
   ```

5. Deploy:

   ```bash
   export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
   export PATH="$JAVA_HOME/bin:$PATH"
   ./mvnw -Prelease clean deploy
   ```

6. After validation, publish the deployment at [central.sonatype.com/publishing/deployments](https://central.sonatype.com/publishing/deployments).

The package is at [central.sonatype.com](https://central.sonatype.com) under `com.formabledocs:formable-sdk`.
