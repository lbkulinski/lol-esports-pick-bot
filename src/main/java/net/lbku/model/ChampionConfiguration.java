package net.lbku.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Value
@Builder(toBuilder = true)
@DynamoDbImmutable(builder = ChampionConfiguration.ChampionConfigurationBuilder.class)
public class ChampionConfiguration {
    @NonNull
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    String id;

    @NonNull
    @Getter
    String displayName;

    @Getter
    boolean enabled;
}
