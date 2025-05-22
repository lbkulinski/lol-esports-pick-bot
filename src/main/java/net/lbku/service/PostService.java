package net.lbku.service;

import io.avaje.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.lbku.model.Champion;
import net.lbku.dto.Game;
import net.lbku.model.PostedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.UnifiedJedis;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Singleton
public final class PostService {
    private final GameService gameService;

    private final TwitterService twitterService;

    private final DynamoDbTable<PostedGame> postedGames;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(PostService.class);
    }

    @Inject
    public PostService(GameService gameService, TwitterService twitterService, DynamoDbEnhancedClient dynamoDbClient) {
        this.gameService = Objects.requireNonNull(gameService);

        this.twitterService = Objects.requireNonNull(twitterService);

        Objects.requireNonNull(dynamoDbClient);

        String postedGamesTableName = Config.get("app.dynamodb.tables.posted-games");

        TableSchema<PostedGame> postedGamesSchema = TableSchema.fromImmutableClass(PostedGame.class);

        this.postedGames = dynamoDbClient.table(postedGamesTableName, postedGamesSchema);
    }

    private TwitterService.TweetStatus tweetGame(Champion champion, Game game) {
        Objects.requireNonNull(champion);

        Objects.requireNonNull(game);

        String player = game.player();

        String tournament = game.tournament();

        String vod = game.vod();

        String text = "%s played %s at %s! %s".formatted(player, champion, tournament, vod);

        return this.twitterService.postTweet(text);
    }

    private void postChampionGames(Champion champion) {
        Objects.requireNonNull(champion);

        List<Game> games = this.gameService.getGames(champion);

        for (Game game : games) {
            String id = game.id();

            Key key = Key.builder()
                         .partitionValue(id)
                         .build();

            PostedGame postedGame = this.postedGames.getItem(key);

            if (postedGame != null) {
                continue;
            }

            TwitterService.TweetStatus status = this.tweetGame(champion, game);

            if (status == TwitterService.TweetStatus.FAILURE) {
                LOGGER.error("Failed to post game with ID {}", id);

                continue;
            }

            long ttl = Instant.now()
                              .plus(30L, ChronoUnit.DAYS)
                              .getEpochSecond();

            PostedGame newGame = PostedGame.builder()
                                           .id(id)
                                           .ttl(ttl)
                                           .build();

            this.postedGames.putItem(newGame);

            LOGGER.info("Posted a new game for {} with ID {}", champion, id);
        }
    }

    public void postNewGames() {
        Champion[] champions = Champion.values();

        Arrays.stream(champions)
              .forEach(this::postChampionGames);
    }
}
