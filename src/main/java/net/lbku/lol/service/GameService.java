package net.lbku.lol.service;

import net.lbku.dto.GameWrapper;
import net.lbku.lol.exception.GameServiceException;
import net.lbku.lol.model.ChampionConfiguration;
import net.lbku.dto.Game;
import net.lbku.dto.GameResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

@Service
public final class GameService {
    private static final String SCHEMA = "https";
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

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public GameService(
        CloseableHttpClient httpClient,
        ObjectMapper objectMapper
    ) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public List<Game> getGames(ChampionConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }

        URI uri = this.buildUri(config);

        HttpGet httpGet = new HttpGet(uri);

        GameResponse gameResponse;

        try {
            gameResponse = this.httpClient.execute(httpGet, this::handleResponse);
        } catch (IOException e) {
            String championName = config.getDisplayName();

            String message = String.format("Failed to fetch game data for champion: %s", championName);

            throw new GameServiceException(message, e);
        }

        List<GameWrapper> wrappers = gameResponse.gameWrappers();

        if ((wrappers == null) || wrappers.isEmpty()) {
            return List.of();
        }

        return gameResponse.gameWrappers()
                           .stream()
                           .map(GameWrapper::game)
                           .toList();
    }

    private URI buildUri(ChampionConfiguration config) {
        String championName = config.getDisplayName();

        String whereClause = String.format(GAME_QUERY_WHERE_TEMPLATE, championName);

        List<NameValuePair> params = List.of(
            new BasicNameValuePair("maxlag","5"),
            new BasicNameValuePair("tables", String.join(",", GAME_QUERY_TABLES)),
            new BasicNameValuePair("limit", "50"),
            new BasicNameValuePair("format", "json"),
            new BasicNameValuePair("order_by", "ScoreboardPlayers.DateTime_UTC DESC"),
            new BasicNameValuePair("action", "cargoquery"),
            new BasicNameValuePair("where", whereClause),
            new BasicNameValuePair("fields", String.join(",",  GAME_QUERY_FIELDS)),
            new BasicNameValuePair("join_on", "ScoreboardPlayers.GameId=ScoreboardGames.GameId")
        );

        URI uri;

        try {
            uri = new URIBuilder()
                .setScheme(SCHEMA)
                .setHost(HOST)
                .setPath(PATH)
                .addParameters(params)
                .build();
        } catch (URISyntaxException e) {
            String message = String.format("Failed to build URI for champion: %s", championName);

            throw new GameServiceException(message, e);
        }

        return uri;
    }

    private GameResponse handleResponse(HttpResponse httpResponse) {
        int statusCode = httpResponse.getStatusLine()
                                     .getStatusCode();

        if (statusCode != HttpStatus.SC_OK) {
            String message = String.format("Received non-OK response: %d", statusCode);

            throw new GameServiceException(message);
        }

        HttpEntity httpEntity = httpResponse.getEntity();

        String stringEntity;

        try {
            stringEntity = EntityUtils.toString(httpEntity);
        } catch (IOException | ParseException e) {
            String message = "Failed to read game data";

            throw new GameServiceException(message, e);
        }

        GameResponse gameResponse;

        try {
            gameResponse = this.objectMapper.readValue(stringEntity, GameResponse.class);
        } catch (JacksonException e) {
            String message = "Failed to deserialize game data";

            throw new GameServiceException(message, e);
        }

        return gameResponse;
    }
}
