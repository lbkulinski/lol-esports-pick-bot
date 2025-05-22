package net.lbku.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Factory
public final class DynamoDbClientFactory {
    @Bean
    public DynamoDbEnhancedClient buildClient() {
        DynamoDbClient client = DynamoDbClient.builder()
                                              .region(Region.US_EAST_2)
                                              .build();

        return DynamoDbEnhancedClient.builder()
                                     .dynamoDbClient(client)
                                     .build();
    }
}
