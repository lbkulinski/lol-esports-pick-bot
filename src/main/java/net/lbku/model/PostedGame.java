package net.lbku.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Value
@Builder(toBuilder = true)
@DynamoDbImmutable(builder = PostedGame.PostedGameBuilder.class)
public class PostedGame {
    @NonNull
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    String id;

    long ttl;
}
