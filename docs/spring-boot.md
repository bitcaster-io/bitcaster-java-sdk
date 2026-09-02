# Spring Boot

The SDK auto-configures a `BitcasterClient` bean when `bitcaster.bae` is present.

## Configuration

```yaml
bitcaster:
  bae: https://API_KEY@example.com/api/o/acme/
  project: orders
  application: checkout
```

Spring Boot relaxed binding also accepts environment variables such as:

```bash
export BITCASTER_BAE="https://API_KEY@example.com/api/o/acme/"
export BITCASTER_PROJECT=orders
export BITCASTER_APPLICATION=checkout
```

## Inject the client

```java
@Service
public class OrderNotifier {
    private final BitcasterClient bitcaster;

    public OrderNotifier(BitcasterClient bitcaster) {
        this.bitcaster = bitcaster;
    }

    public void notifyCreated(String orderId) {
        bitcaster.triggerEvent(
                "order.created",
                Map.of("orderId", orderId),
                null,
                null);
    }
}
```

The client is only auto-created when `bitcaster.bae` is configured. Define your own `BitcasterClient` bean to replace the default.

!!! warning "Keep tokens private"
    Never commit a BAE containing a real API key. Prefer environment variables or a secret manager in deployed applications.