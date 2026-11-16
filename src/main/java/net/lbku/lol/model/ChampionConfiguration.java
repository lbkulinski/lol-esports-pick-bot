package net.lbku.lol.model;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

@Builder(toBuilder = true)
@DynamoDbImmutable(builder = ChampionConfiguration.ChampionConfigurationBuilder.class)
@NullMarked
public record ChampionConfiguration(
    @DynamoDbPartitionKey String id,
    String displayName,
    boolean enabled
) {
    public ChampionConfiguration {
        Objects.requireNonNull(id);
        Objects.requireNonNull(displayName);
    }
}
