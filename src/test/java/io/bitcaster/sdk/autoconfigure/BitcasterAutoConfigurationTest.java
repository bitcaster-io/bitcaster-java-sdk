package io.bitcaster.sdk.autoconfigure;

import io.bitcaster.sdk.BitcasterClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BitcasterAutoConfigurationTest {
    private static final String BAE = "https://token-123@bitcaster.example.com/api/o/demo-org/";

    @Test
    void bindsPropertiesAndConfiguresDomain() {
        BitcasterProperties properties = new BitcasterProperties();
        properties.setBae(BAE);
        properties.setProject("demo-project");
        properties.setApplication("demo-app");
        properties.setDistributionList("alerts");

        assertEquals("alerts", properties.getDistributionList());
        BitcasterClient client = new BitcasterAutoConfiguration().bitcasterClient(properties);

        assertEquals("https://bitcaster.example.com/api/o/demo-org/", client.getBaseUrl());
    }

    @Test
    void leavesDomainUnsetWhenOptionalPropertiesAreBlank() {
        BitcasterProperties properties = new BitcasterProperties();
        properties.setBae(BAE);

        BitcasterClient client = new BitcasterAutoConfiguration().bitcasterClient(properties);

        assertEquals("https://bitcaster.example.com/api/o/demo-org/", client.getBaseUrl());
    }

    @Test
    void leavesDomainUnsetWhenOnlyOneDomainPropertyIsConfigured() {
        BitcasterProperties properties = new BitcasterProperties();
        properties.setBae(BAE);
        properties.setProject("demo-project");

        BitcasterClient client = new BitcasterAutoConfiguration().bitcasterClient(properties);

        assertEquals("https://bitcaster.example.com/api/o/demo-org/", client.getBaseUrl());
    }
}