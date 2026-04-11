package net.lbku.service;

import net.lbku.aws.client.AwsSecretsClient;
import net.lbku.bluesky.dto.BlueskyPost;
import net.lbku.bluesky.dto.BlueskySession;
import net.lbku.lol.model.ChampionConfiguration;
import net.lbku.lol.dto.Game;
import net.lbku.lol.model.PostedGame;
import net.lbku.lol.repository.ChampionConfigurationRepository;
import net.lbku.lol.client.LolFandomClient;
import net.lbku.lol.repository.PostedGameRepository;
import net.lbku.mediawiki.client.MediaWikiClient;
import net.lbku.bluesky.client.BlueskyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public final class PostService {
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    private static final String POST_TEMPLATE = "%s played %s at %s! %s";

    private final ChampionConfigurationRepository configurationRepository;
    private final MediaWikiClient mediaWikiClient;
    private final AwsSecretsClient awsSecretsClient;
    private final BlueskyClient blueskyClient;
    private final LolFandomClient lolFandomClient;
    private final PostedGameRepository postedGameRepository;

    private final String blueskyHandle;

    @Autowired
    public PostService(
        ChampionConfigurationRepository configurationRepository,
        MediaWikiClient mediaWikiClient,
        AwsSecretsClient awsSecretsClient,
        BlueskyClient blueskyClient,
        LolFandomClient lolFandomClient,
        PostedGameRepository postedGameRepository,
        @Value("${app.bluesky.handle}") String blueskyHandle
    ) {
        this.configurationRepository = configurationRepository;
        this.mediaWikiClient = mediaWikiClient;
        this.awsSecretsClient = awsSecretsClient;
        this.blueskyClient = blueskyClient;
        this.lolFandomClient = lolFandomClient;
        this.postedGameRepository = postedGameRepository;
        this.blueskyHandle = blueskyHandle;
    }

    public void postNewGames() {
        String loginToken = this.mediaWikiClient.getLoginToken();

        this.mediaWikiClient.login(loginToken);

        String blueskyAppPassword = this.awsSecretsClient.getAppSecret()
                                                         .bluesky()
                                                         .appPassword();

        BlueskySession session = this.blueskyClient.createSession(this.blueskyHandle, blueskyAppPassword);

        this.configurationRepository.findAll()
                                    .forEach(configuration -> this.postChampionGames(session, configuration));
    }

    private void postChampionGames(BlueskySession blueskySession, ChampionConfiguration configuration) {
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

            this.postGame(blueskySession, configuration, game);

            PostedGame newGame = PostedGame.builder()
                                           .id(id)
                                           .build();

            this.postedGameRepository.save(newGame);

            String championName = configuration.getDisplayName();

            log.info("Posted a new game for {} with ID {}", championName, id);
        }
    }

    private void postGame(BlueskySession blueskySession, ChampionConfiguration configuration, Game game) {
        String championName = configuration.getDisplayName();

        String text = String.format(POST_TEMPLATE, game.player(), championName, game.tournament(), game.vod());

        BlueskyPost blueskyPost = this.blueskyClient.post(blueskySession, text);

        log.info("Successfully posted game with ID {} to Bluesky, post URI: {}", game.id(), blueskyPost.uri());
    }
}
