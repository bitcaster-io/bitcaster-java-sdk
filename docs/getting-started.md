# Getting started

## Add the dependency

Add the SDK to your Maven project:

```xml
<dependency>
    <groupId>io.bitcaster</groupId>
    <artifactId>bitcaster-java-sdk</artifactId>
    <version>0.3.0</version>
</dependency>
```

## Configure the endpoint

The BAE endpoint has this form:

```text
https://API_KEY@HOST/api/o/ORGANIZATION/
```

Create a client and bind it to a project/application domain:

```java
var client = new BitcasterClient(
        "https://API_KEY@example.com/api/o/acme/");
client.setDomain("orders", "checkout");
```

Project, application, event, username, and email path values are URL-encoded before requests are sent.

## Trigger an event

```java
var response = client.triggerEvent(
        "order.created",
        Map.of("orderId", "abc-123", "currency", "EUR"),
        Map.of("priority", "high"),
        "checkout-123");
```

`context` and `options` may be `null`; they are serialized as empty JSON objects. The correlation ID is optional.

## Run asynchronously

Use the async facade when the calling thread should not wait for the HTTP request:

```java
try (var client = new AsyncBitcasterClient(
        "https://API_KEY@example.com/api/o/acme/")) {
    client.setDomain("orders", "checkout");
    var response = client.triggerEvent(
            "order.created", Map.of("orderId", "abc-123"), null, null)
        .join();
}
```

Supply an application-managed `Executor` when you need control over the worker pool.