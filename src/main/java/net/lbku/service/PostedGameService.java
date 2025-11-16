package net.lbku.service;

import net.lbku.model.PostedGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Objects;

@Service
public final class PostedGameService {
    private final DynamoDbTable<PostedGame> postedGames;

    @Autowired
    public PostedGameService(
        DynamoDbEnhancedClient dynamoDbClient,
        @Value("${app.aws.dynamodb.tables.posted-games}") String tableName
    ) {
        TableSchema<PostedGame> tableSchema = TableSchema.fromImmutableClass(PostedGame.class);

        this.postedGames = dynamoDbClient.table(tableName, tableSchema);
    }

    public void createPostedGame(PostedGame postedGame) {
        Objects.requireNonNull(postedGame);

        this.postedGames.putItem(postedGame);
    }

    public PostedGame getPostedGame(String gameId) {
        Objects.requireNonNull(gameId);

        Key key = Key.builder()
                     .partitionValue(gameId)
                     .build();

        return this.postedGames.getItem(key);
    }
}
