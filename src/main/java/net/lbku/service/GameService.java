package net.lbku.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import net.lbku.model.Champion;
import net.lbku.model.Game;
import net.lbku.model.GameResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class GameService {
    private final ObjectMapper mapper;

    @Inject
    public GameService(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
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

                                          String string = "%s=%s".formatted(key, encodedValue);

                                          return string;
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
            throw new RuntimeException(e);
        }

        return response.body();
    }

    public List<Game> getGames(Champion champion) {
        Objects.requireNonNull(champion);

        URI uri = this.buildUri(champion);

        String jsonData = this.getJsonData(uri);

        GameResponse gameResponse;

        try {
            gameResponse = this.mapper.readValue(jsonData, GameResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Game> games = gameResponse.games();

        List<Game> copy = new ArrayList<>(games);

        copy.sort(Comparator.comparing(Game::timestamp));

        return List.copyOf(copy);
    }
}
