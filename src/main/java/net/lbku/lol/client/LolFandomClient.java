package net.lbku.lol.client;

import net.lbku.lol.dto.GameWrapper;
import net.lbku.lol.exception.LolFandomException;
import net.lbku.lol.model.ChampionConfiguration;
import net.lbku.lol.dto.Game;
import net.lbku.lol.dto.GameResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@NullMarked
public final class LolFandomClient {
    private static final String SCHEME = "https";
    private static final String HOST = "lol.fandom.com";
    private static final String PATH = "/api.php";

    private static final Set<String> GAME_QUERY_TABLES = Set.of(
        "ScoreboardPlayers",
        "ScoreboardGames"
    );

    private static final String GAME_QUERY_WHERE_TEMPLATE = """
        ScoreboardPlayers.Champion = '%s' \
        AND ScoreboardGames.VOD IS NOT NULL AND ScoreboardGames.VOD != ''""";

    private static final Set<String> GAME_QUERY_FIELDS = Set.of(
        "ScoreboardPlayers.GameId",
        "ScoreboardPlayers.Link",
        "ScoreboardGames.Tournament",
        "ScoreboardPlayers.DateTime_UTC",
        "ScoreboardPlayers.PlayerWin",
        "ScoreboardGames.VOD"
    );

    private final RestClient restClient;

    @Autowired
    public LolFandomClient(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public List<Game> getGames(ChampionConfiguration config) {
        Objects.requireNonNull(config);

        URI uri = this.buildUri(config);

        GameResponse gameResponse;

        try {
            gameResponse = this.restClient.get()
                                          .uri(uri)
                                          .retrieve()
                                          .requiredBody(GameResponse.class);
        } catch (RestClientException | IllegalStateException e) {
            String message = "Failed to fetch game data for champion: %s".formatted(config.displayName());

            throw new LolFandomException(message, e);
        }

        List<GameWrapper> wrappers = gameResponse.gameWrappers();

        if (wrappers.isEmpty()) {
            return List.of();
        }

        return gameResponse.gameWrappers()
                           .stream()
                           .map(GameWrapper::game)
                           .toList();
    }

    private URI buildUri(ChampionConfiguration config) {
        var whereClause = GAME_QUERY_WHERE_TEMPLATE.formatted(config.displayName());

        var params = new LinkedMultiValueMap<String, String>();

        params.add("maxlag","5");
        params.add("tables", String.join(",", GAME_QUERY_TABLES));
        params.add("limit", "50");
        params.add("format", "json");
        params.add("order_by", "ScoreboardPlayers.DateTime_UTC DESC");
        params.add("action", "cargoquery");
        params.add("where", whereClause);
        params.add("fields", String.join(",",  GAME_QUERY_FIELDS));
        params.add("join_on", "ScoreboardPlayers.GameId=ScoreboardGames.GameId");

        return UriComponentsBuilder.newInstance()
                                   .scheme(SCHEME)
                                   .host(HOST)
                                   .path(PATH)
                                   .queryParams(params)
                                   .build()
                                   .toUri();
    }
}
