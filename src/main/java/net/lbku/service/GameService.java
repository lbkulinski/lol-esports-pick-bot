package net.lbku.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.lbku.dto.GameWrapper;
import net.lbku.exception.GameServiceException;
import net.lbku.model.ChampionConfiguration;
import net.lbku.dto.Game;
import net.lbku.dto.GameResponse;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
        Objects.requireNonNull(config);

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

    private GameResponse handleResponse(ClassicHttpResponse httpResponse) {
        int statusCode = httpResponse.getCode();

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
        } catch (IOException e) {
            String message = "Failed to deserialize game data";

            throw new GameServiceException(message, e);
        }

        return gameResponse;
    }
}
