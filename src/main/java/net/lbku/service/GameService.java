package net.lbku.service;

import io.avaje.jsonb.Jsonb;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.lbku.dto.GameWrapper;
import net.lbku.model.Champion;
import net.lbku.dto.Game;
import net.lbku.dto.GameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Singleton
public final class GameService {
    private final Jsonb jsonb;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(GameService.class);
    }

    @Inject
    public GameService(Jsonb jsonb) {
        this.jsonb = Objects.requireNonNull(jsonb);
    }

    private URI buildUri(Champion champion) {
        Objects.requireNonNull(champion);

        Set<String> tables = Set.of(
            "ScoreboardPlayers",
            "ScoreboardGames"
        );

        String where = """
        ScoreboardPlayers.Champion = '%s' \
        AND ScoreboardGames.VOD IS NOT NULL AND ScoreboardGames.VOD != ''""".formatted(champion);

        Set<String> fields = Set.of(
            "ScoreboardPlayers.GameId",
            "ScoreboardPlayers.Link",
            "ScoreboardGames.Tournament",
            "ScoreboardPlayers.DateTime_UTC",
            "ScoreboardPlayers.PlayerWin",
            "ScoreboardGames.VOD"
        );

        Map<String, String> queryParameters = Map.of(
            "maxlag", "5",
            "tables", String.join(",", tables),
            "limit", "50",
            "format", "json",
            "order_by", "ScoreboardPlayers.DateTime_UTC DESC",
            "action", "cargoquery",
            "where", where,
            "fields", String.join(",",  fields),
            "join_on", "ScoreboardPlayers.GameId=ScoreboardGames.GameId"
        );

        String query = queryParameters.entrySet()
                                      .stream()
                                      .map(entry -> {
                                          String key = entry.getKey();

                                          String value = entry.getValue();

                                          String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);

                                          return "%s=%s".formatted(key, encodedValue);
                                      })
                                      .reduce("%s&%s"::formatted)
                                      .get();

        String uriString = "https://lol.fandom.com/api.php?%s".formatted(query);

        return URI.create(uriString);
    }

    private String getJsonData(URI uri) {
        Objects.requireNonNull(uri);

        HttpRequest request = HttpRequest.newBuilder(uri)
                                         .GET()
                                         .build();

        HttpResponse<String> response;

        HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

        try (HttpClient client = HttpClient.newHttpClient()) {
            response = client.send(request, bodyHandler);
        } catch (IOException | InterruptedException e) {
            String message = e.getMessage();

            LOGGER.error(message, e);

            return null;
        }

        return response.body();
    }

    public List<Game> getGames(Champion champion) {
        Objects.requireNonNull(champion);

        URI uri = this.buildUri(champion);

        String jsonData = this.getJsonData(uri);

        if (jsonData == null) {
            return List.of();
        }

        List<Game> games = this.jsonb.type(GameResponse.class)
                                     .fromJson(jsonData)
                                     .gameWrappers()
                                     .stream()
                                     .map(GameWrapper::game)
                                     .toList();

        return List.copyOf(games);
    }
}
