# Contributing

## Development

This project requires Java 17 or newer. Use the Maven Wrapper from the project
root so the expected Maven version is used:

```bash
./mvnw test
./mvnw -Pcoverage verify
```

The focused client tests can be run with:

```bash
./mvnw test -Dtest=BitcasterClientTest
```

## Pull Requests

- Keep changes focused and preserve the Python SDK's REST paths, payloads,
  authentication, and error behavior.
- Add or update tests for changed behavior, including request paths, headers,
  payloads, and error mapping where applicable.
- Do not commit `target/`, Maven wrapper JARs, credentials, or generated files.
- Update the README or other documentation when the public API or workflow
  changes.
- Use the `main` branch as the base for pull requests.

Before opening a pull request, run the test and coverage commands above and
review the resulting diff for unrelated changes.