package net.lbku.service;

import com.google.inject.Inject;
import net.lbku.client.TwitterClient;
import net.lbku.model.Champion;
import net.lbku.model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.UnifiedJedis;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class PostService {
    private final GameService gameService;

    private final UnifiedJedis jedis;

    private final TwitterClient twitterClient;

    private static final String SET_NAME;

    private static final Logger LOGGER;

    static {
        SET_NAME = "seen_vods";

        LOGGER = LoggerFactory.getLogger(PostService.class);
    }

    @Inject
    public PostService(GameService gameService, UnifiedJedis jedis, TwitterClient twitterClient) {
        this.gameService = Objects.requireNonNull(gameService);

        this.jedis = Objects.requireNonNull(jedis);

        this.twitterClient = Objects.requireNonNull(twitterClient);
    }

    private TwitterClient.TweetStatus tweetGame(Champion champion, Game game) {
        Objects.requireNonNull(champion);

        Objects.requireNonNull(game);

        String player = game.player();

        String tournament = game.tournament();

        String vod = game.vod();

        String text = "%s played %s at %s! %s".formatted(player, champion, tournament, vod);

        return this.twitterClient.postTweet(text);
    }

    private void postChampionGames(Champion champion) {
        Objects.requireNonNull(champion);

        List<Game> games = this.gameService.getGames(champion);

        for (Game game : games) {
            String id = game.id();

            if (this.jedis.sismember(PostService.SET_NAME, id)) {
                continue;
            }

            TwitterClient.TweetStatus status = this.tweetGame(champion, game);

            if (status == TwitterClient.TweetStatus.SUCCESS) {
                this.jedis.sadd(PostService.SET_NAME, id);

                continue;
            }

            PostService.LOGGER.error("Failed to post game with ID {}", id);
        }
    }

    public void postNewGames() {
        Champion[] champions = Champion.values();

        Arrays.stream(champions)
              .forEach(this::postChampionGames);
    }
}
