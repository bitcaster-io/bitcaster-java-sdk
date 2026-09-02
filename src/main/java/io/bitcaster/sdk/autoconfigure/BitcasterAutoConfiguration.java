package io.bitcaster.sdk.autoconfigure;

import io.bitcaster.sdk.BitcasterClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(BitcasterProperties.class)
@ConditionalOnProperty(prefix = "bitcaster", name = "bae")
public class BitcasterAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    BitcasterClient bitcasterClient(BitcasterProperties properties) {
        BitcasterClient client = new BitcasterClient(properties.getBae());
        if (!properties.getProject().isBlank() && !properties.getApplication().isBlank()) {
            client.setDomain(properties.getProject(), properties.getApplication());
        }
        return client;
    }
}