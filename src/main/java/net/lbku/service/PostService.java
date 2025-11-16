package net.lbku.service;

import net.lbku.model.ChampionConfiguration;
import net.lbku.dto.Game;
import net.lbku.model.PostedGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public final class PostService {
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private static final String TWEET_TEMPLATE = "%s played %s at %s! %s";

    private final ChampionConfigurationService configurationService;
    private final GameService gameService;
    private final PostedGameService postedGameService;
    private final TwitterService twitterService;

    @Autowired
    public PostService(
        ChampionConfigurationService configurationService,
        GameService gameService,
        PostedGameService postedGameService,
        TwitterService twitterService
    ) {
        this.configurationService = configurationService;
        this.gameService = gameService;
        this.postedGameService = postedGameService;
        this.twitterService = twitterService;
    }

    public void postNewGames() {
        this.configurationService.getConfigurations()
                                 .forEach(this::postChampionGames);
    }

    private void postChampionGames(ChampionConfiguration configuration) {
        Objects.requireNonNull(configuration);

        List<Game> games = this.gameService.getGames(configuration);

        for (Game game : games) {
            String id = game.id();

            PostedGame postedGame = this.postedGameService.getPostedGame(id);

            if (postedGame != null) {
                continue;
            }

            this.tweetGame(configuration, game);

            PostedGame newGame = PostedGame.builder()
                                           .id(id)
                                           .build();

            this.postedGameService.createPostedGame(newGame);

            String championName = configuration.getDisplayName();

            log.info("Posted a new game for {} with ID {}", championName, id);
        }
    }

    private void tweetGame(ChampionConfiguration configuration, Game game) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(game);

        String championName = configuration.getDisplayName();

        String text = String.format(TWEET_TEMPLATE, game.player(), championName, game.tournament(), game.vod());

        this.twitterService.postTweet(text);
    }
}
