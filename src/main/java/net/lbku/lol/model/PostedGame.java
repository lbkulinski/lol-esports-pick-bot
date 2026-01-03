package net.lbku.lol.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.jspecify.annotations.NullMarked;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Value
@Builder(toBuilder = true)
@NullMarked
@DynamoDbImmutable(builder = PostedGame.PostedGameBuilder.class)
public class PostedGame {
    @Getter(onMethod_ = @DynamoDbPartitionKey)
    String id;
}
