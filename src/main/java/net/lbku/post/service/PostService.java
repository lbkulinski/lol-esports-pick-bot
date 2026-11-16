package net.lbku.post.service;

import lombok.extern.slf4j.Slf4j;
import net.lbku.aws.client.SecretsClient;
import net.lbku.aws.dto.Secret;
import net.lbku.bluesky.dto.ExternalLink;
import net.lbku.bluesky.dto.Record;
import net.lbku.bluesky.dto.Session;
import net.lbku.lol.model.ChampionConfiguration;
import net.lbku.lol.dto.Game;
import net.lbku.lol.model.PostedGame;
import net.lbku.lol.repository.ChampionConfigurationRepository;
import net.lbku.lol.client.LolFandomClient;
import net.lbku.lol.repository.PostedGameRepository;
import net.lbku.bluesky.client.BlueskyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public final class PostService {
    private static final String POST_TEMPLATE = "%s played %s at %s!";

    private final SecretsClient secretsClient;
    private final ChampionConfigurationRepository configRepository;
    private final LolFandomClient lolFandomClient;
    private final PostedGameRepository postedGameRepository;
    private final BlueskyClient blueskyClient;

    @Autowired
    public PostService(
        SecretsClient secretsClient,
        ChampionConfigurationRepository configRepository,
        LolFandomClient lolFandomClient,
        PostedGameRepository postedGameRepository,
        BlueskyClient blueskyClient
    ) {
        this.secretsClient = secretsClient;
        this.configRepository = configRepository;
        this.lolFandomClient = lolFandomClient;
        this.postedGameRepository = postedGameRepository;
        this.blueskyClient = blueskyClient;
    }

    public void postNewGames() {
        Secret.BlueskySecret secret = this.secretsClient.getAppSecret().bluesky();

        Session session = this.blueskyClient.createSession(secret.identifier(), secret.appPassword());

        this.configRepository.findAll()
                             .forEach(config -> this.postChampionGames(session, config));
    }

    private void postChampionGames(Session session, ChampionConfiguration config) {
        if (!config.enabled()) {
            return;
        }

        List<Game> games = this.lolFandomClient.getGames(config);

        var copy = new ArrayList<>(games);

        copy.sort(Comparator.comparing(Game::timestamp));

        for (Game game : copy) {
            String id = game.id();

            Optional<PostedGame> optionalGame = this.postedGameRepository.findById(id);

            if (optionalGame.isPresent()) {
                continue;
            }

            Record record = this.postGame(session, config, game);

            var newGame = PostedGame.builder()
                                    .id(id)
                                    .build();

            this.postedGameRepository.save(newGame);

            String championName = config.displayName();

            log.info("Posted a new game for {} with ID {}", championName, record.cid());

            try {
                Thread.sleep(10_000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new RuntimeException(e);
            }
        }
    }

    private Record postGame(Session session, ChampionConfiguration config, Game game) {
        var text = POST_TEMPLATE.formatted(game.player(), config.displayName(), game.tournament());
        var link = new ExternalLink(game.vod(), "", "");

        return this.blueskyClient.createRecord(session, text, link);
    }
}
