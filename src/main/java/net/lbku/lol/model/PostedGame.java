package net.lbku.lol.model;

import lombok.Builder;
import org.jspecify.annotations.NullMarked;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

@Builder(toBuilder = true)
@DynamoDbImmutable(builder = PostedGame.PostedGameBuilder.class)
@NullMarked
public record PostedGame(@DynamoDbPartitionKey String id) {
    public PostedGame {
        Objects.requireNonNull(id);
    }
}
