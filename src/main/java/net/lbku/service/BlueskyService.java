package net.lbku.service;

import com.google.inject.Inject;
import net.lbku.model.Champion;
import net.lbku.model.Game;
import redis.clients.jedis.UnifiedJedis;

import java.util.List;
import java.util.Objects;

public final class BlueskyService {
    private final GameService gameService;

    private final UnifiedJedis jedis;

    private static final String SET_NAME;

    static {
        SET_NAME = "seen_vods";
    }

    @Inject
    public BlueskyService(GameService gameService, UnifiedJedis jedis) {
        this.gameService = Objects.requireNonNull(gameService);

        this.jedis = Objects.requireNonNull(jedis);
    }

    public void postNewGames() {
        List<Game> games = this.gameService.getGames(Champion.DRAVEN);

        for (Game game : games) {
            String id = game.id();

            if (this.jedis.sismember(BlueskyService.SET_NAME, id)) {
                System.out.printf("Old game: %s\n", game);

                continue;
            }

            this.jedis.sadd(BlueskyService.SET_NAME, id);

            System.out.printf("New game: %s\n", game);
        }
    }
}
