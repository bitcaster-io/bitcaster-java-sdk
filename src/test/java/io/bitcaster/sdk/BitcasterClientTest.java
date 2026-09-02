package io.bitcaster.sdk;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BitcasterClientTest {
    @Test
    void parsesEndpointWithoutExposingTokenInBaseUrl() {
        BitcasterClient client = new BitcasterClient(
                "https://token-123@bitcaster.example.com/api/o/demo-org");

        assertEquals("https://bitcaster.example.com/api/o/demo-org/", client.getBaseUrl());
    }

    @Test
    void rejectsMissingDomainWhenTriggering() {
        BitcasterClient client = new BitcasterClient(
                "https://token-123@bitcaster.example.com/api/o/demo-org/");

        assertThrows(BitcasterConfigurationException.class,
                () -> client.triggerEvent("signup", Map.of(), null, null));
    }

    @Test
    void rejectsMalformedEndpoint() {
        assertThrows(BitcasterConfigurationException.class,
                () -> new BitcasterClient("https://example.com/api/v1/"));
    }
}