# Bitcaster Java SDK

Spring-friendly Java client for the Bitcaster REST API. Java 17 and Spring Boot 3.5 are supported.

## Usage

```java
BitcasterClient client = new BitcasterClient(
        "https://API_KEY@example.com/api/o/my-organization/");
client.setDomain("my-project", "my-application");

Map<String, Object> result = client.triggerEvent(
        "order.created", Map.of("id", "abc-123"), null, null);
List<Map<String, Object>> users = client.listUsers();
```

The client sends the same requests as the Python SDK. It supports `ping`, resource
listing, event triggering, user creation/update, application registration, and
unregistration. `AsyncBitcasterClient` exposes the same mutating operations as
`CompletableFuture` values.

## Spring Boot

The auto-configuration creates a `BitcasterClient` bean when the starter is on the
classpath. Configure it with `bitcaster.bae`, `bitcaster.project`, and
`bitcaster.application`; environment variables such as `BITCASTER_BAE` work through
Spring Boot's relaxed binding.

```yaml
bitcaster:
  bae: https://API_KEY@example.com/api/o/my-organization/
  project: my-project
  application: my-application
```

The endpoint token is removed from request URLs and sent as `Authorization: Key ...`.