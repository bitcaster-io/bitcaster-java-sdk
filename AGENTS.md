# Agent Instructions

## Project

- This is the Bitcaster Java SDK, a Spring Boot-compatible library.
- Use Java 17 language features and Spring Boot 3.5 APIs.
- Keep the public API framework-friendly: prefer constructor injection, immutable value types, and `CompletableFuture` for asynchronous operations.
- Preserve parity with the Python SDK's REST paths, request payloads, authentication, and error behavior.

## Build and Test

- Use the Maven Wrapper, not a system Maven installation:
  - macOS/Linux: `./mvnw <command>`
  - Windows: `mvnw.cmd <command>`
- Run the complete test suite with `./mvnw test`.
- Package the library with `./mvnw package`.
- Run a focused test with `./mvnw test -Dtest=BitcasterClientTest`.
- Do not commit `target/` or `.mvn/wrapper/maven-wrapper.jar`.

## Code Layout

- Production code belongs under `src/main/java/io/bitcaster/sdk`.
- Spring Boot auto-configuration belongs under `io.bitcaster.sdk.autoconfigure`.
- Resources for auto-configuration metadata belong under `src/main/resources/META-INF`.
- Tests belong under `src/test/java` and should verify request paths, headers, payloads, and error mapping.

## Implementation Guidelines

- Use `RestClient` for synchronous HTTP operations and delegate asynchronous work to an injected executor.
- Never expose the BAE token in a URL or log message; send it only as `Authorization: Key <token>`.
- URL-encode user-controlled path segments and query parameters.
- Map non-success HTTP responses to typed `BitcasterException` subclasses.
- Keep null handling compatible with the Python SDK: optional maps and collections serialize as empty objects or arrays where the API expects them.
- Avoid adding dependencies unless they are required by the public API or Spring Boot integration.

## Validation

Before finishing a change:

1. Run `./mvnw test`.
2. Check editor/compiler diagnostics for changed Java files.
3. Review `git diff` for unrelated changes, generated files, or accidental credentials.
