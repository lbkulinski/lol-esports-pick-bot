package net.lbku.lol.repository;

import net.lbku.lol.model.PostedGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public final class PostedGameRepository {
    private final DynamoDbTable<PostedGame> postedGames;

    @Autowired
    public PostedGameRepository(
        DynamoDbEnhancedClient dynamoDbClient,
        @Value("${app.aws.dynamodb.tables.posted-games}") String tableName
    ) {
        TableSchema<PostedGame> tableSchema = TableSchema.fromImmutableClass(PostedGame.class);

        this.postedGames = dynamoDbClient.table(tableName, tableSchema);
    }

    public void save(PostedGame entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }

        this.postedGames.putItem(entity);
    }

    public Optional<PostedGame> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        Key key = Key.builder()
                     .partitionValue(id)
                     .build();

        PostedGame postedGame = this.postedGames.getItem(key);

        return Optional.ofNullable(postedGame);
    }
}
