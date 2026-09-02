# Signals in. Messages out.

The **Bitcaster Java SDK** is a Spring-friendly Java 17 client for the Bitcaster notification platform.

It gives applications one small, predictable API for triggering events, managing users, and listing Bitcaster resources. It supports both blocking calls and `CompletableFuture`-based asynchronous operations.

!!! tip "Start here"
    Follow the [getting started guide](getting-started.md), or jump straight to the [Spring Boot setup](spring-boot.md).

## What you get

| Capability | Description |
| --- | --- |
| Event delivery | Trigger events in a configured project and application |
| User management | Create, update, register, and unregister users |
| Resource discovery | List projects, applications, events, users, and distribution lists |
| Async operations | Run SDK operations through `CompletableFuture` |
| Spring Boot integration | Auto-configured `BitcasterClient` bean |
| Predictable failures | Typed exceptions with HTTP status information |

## A first event

```java
var client = new BitcasterClient(
        "https://API_KEY@example.com/api/o/acme/");
client.setDomain("orders", "checkout");

var result = client.triggerEvent(
        "order.created",
        Map.of("orderId", "abc-123"),
        null,
        null);
```

The API token is never placed in a request URL. The client sends it as `Authorization: Key API_KEY`.

## Project links

- [Source code](https://github.com/bitcaster-io/bitcaster-java-sdk)
- [Releases](https://github.com/bitcaster-io/bitcaster-java-sdk/releases)
- [Security policy](https://github.com/bitcaster-io/bitcaster-java-sdk/blob/main/.github/SECURITY.md)