package net.lbku.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

@Factory
public final class SecretsManagerClientFactory {
    @Bean
    public SecretsManagerClient buildClient() {
        return SecretsManagerClient.builder()
                                   .region(Region.US_EAST_2)
                                   .build();
    }
}
