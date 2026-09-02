# Testing and releases

## Run tests

Run the complete suite with the Maven Wrapper:

```bash
./mvnw test
```

Run the focused client contract tests:

```bash
./mvnw test -Dtest=BitcasterClientTest
```

## Coverage gate

JaCoCo is opt-in for normal development and enforces an 80% branch-coverage minimum:

```bash
./mvnw -Pcoverage verify
```

The HTML report is generated at `target/site/jacoco/index.html`. The coverage profile exercises the SDK by executing requests against a mock HTTP server, including URL encoding, authentication, payload defaults, error mapping, and asynchronous calls.

## Prepare a release

Update the Maven version, validate it, and tag the release:

```bash
./mvnw versions:set -DnewVersion=0.4.0
./mvnw versions:commit
./mvnw clean verify
git tag -a v0.4.0 -m "Release v0.4.0"
git push origin main
git push origin v0.4.0
```

See [VERSIONING.md](https://github.com/bitcaster-io/bitcaster-java-sdk/blob/main/VERSIONING.md) for the full versioning policy.