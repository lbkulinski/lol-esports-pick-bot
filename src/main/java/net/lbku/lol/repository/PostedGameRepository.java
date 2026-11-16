package net.lbku.lol.repository;

import net.lbku.lol.model.PostedGame;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Objects;
import java.util.Optional;

@Repository
@NullMarked
public final class PostedGameRepository {
    private final DynamoDbTable<@Nullable PostedGame> postedGames;

    @Autowired
    public PostedGameRepository(
        DynamoDbEnhancedClient dynamoDbClient,
        @Value("${app.aws.dynamodb.tables.posted-games}") String tableName
    ) {
        TableSchema<PostedGame> tableSchema = TableSchema.fromImmutableClass(PostedGame.class);

        this.postedGames = dynamoDbClient.table(tableName, tableSchema);
    }

    public void save(PostedGame postedGame) {
        Objects.requireNonNull(postedGame);

        this.postedGames.putItem(postedGame);
    }

    public Optional<PostedGame> findById(String id) {
        Objects.requireNonNull(id);

        Key key = Key.builder()
                     .partitionValue(id)
                     .build();

        PostedGame postedGame = this.postedGames.getItem(key);

        return Optional.ofNullable(postedGame);
    }
}
