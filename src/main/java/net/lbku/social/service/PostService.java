package net.lbku.social.service;

import net.lbku.lol.model.ChampionConfiguration;
import net.lbku.lol.dto.Game;
import net.lbku.lol.model.PostedGame;
import net.lbku.lol.repository.ChampionConfigurationRepository;
import net.lbku.lol.client.LolFandomClient;
import net.lbku.lol.repository.PostedGameRepository;
import net.lbku.mediawiki.client.MediaWikiClient;
import net.lbku.social.client.TwitterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public final class PostService {
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private static final String TWEET_TEMPLATE = "%s played %s at %s! %s";

    private final ChampionConfigurationRepository configurationRepository;
    private final MediaWikiClient mediaWikiClient;
    private final LolFandomClient lolFandomClient;
    private final PostedGameRepository postedGameRepository;
    private final TwitterClient twitterClient;

    @Autowired
    public PostService(
        ChampionConfigurationRepository configurationRepository,
        MediaWikiClient mediaWikiClient,
        LolFandomClient lolFandomClient,
        PostedGameRepository postedGameRepository,
        TwitterClient twitterClient
    ) {
        this.configurationRepository = configurationRepository;
        this.mediaWikiClient = mediaWikiClient;
        this.lolFandomClient = lolFandomClient;
        this.postedGameRepository = postedGameRepository;
        this.twitterClient = twitterClient;
    }

    public void postNewGames() {
        String loginToken = this.mediaWikiClient.getLoginToken();

        this.mediaWikiClient.login(loginToken);

        this.configurationRepository.findAll()
                                    .forEach(this::postChampionGames);
    }

    private void postChampionGames(ChampionConfiguration configuration) {
        if (!configuration.isEnabled()) {
            return;
        }

        List<Game> games = this.lolFandomClient.getGames(configuration);

        for (Game game : games) {
            String id = game.id();

            Optional<PostedGame> optionalGame = this.postedGameRepository.findById(id);

            if (optionalGame.isPresent()) {
                continue;
            }

            this.tweetGame(configuration, game);

            PostedGame newGame = PostedGame.builder()
                                           .id(id)
                                           .build();

            this.postedGameRepository.save(newGame);

            String championName = configuration.getDisplayName();

            log.info("Posted a new game for {} with ID {}", championName, id);
        }
    }

    private void tweetGame(ChampionConfiguration configuration, Game game) {
        String championName = configuration.getDisplayName();

        String text = String.format(TWEET_TEMPLATE, game.player(), championName, game.tournament(), game.vod());

        this.twitterClient.postTweet(text);
    }
}
