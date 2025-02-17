package net.lbku.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.lbku.model.Champion;
import net.lbku.model.Game;
import net.lbku.model.GameResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

public final class GameService {
    public List<Game> getGames(Champion champion) {
        String template = """
        https://lol.fandom.com//api.php\
        ?maxlag=5\
        &tables=%s\
        &limit=500\
        &format=json\
        &order_by=ScoreboardPlayers.DateTime_UTC+DESC\
        &action=cargoquery\
        &where=ScoreboardPlayers.Champion='%s'\
        &fields=%s\
        &join_on=ScoreboardPlayers.GameId=ScoreboardGames.GameId""";

        Set<String> tables = Set.of("ScoreboardPlayers", "ScoreboardGames");

        Set<String> fields = Set.of("ScoreboardPlayers.GameId", "ScoreboardPlayers.Link", "ScoreboardGames.Tournament",
            "ScoreboardPlayers.DateTime_UTC", "ScoreboardPlayers.PlayerWin", "ScoreboardGames.VOD");

        String uriString = template.formatted(String.join(",", tables), champion,
            String.join(",", fields));

        URI uri = URI.create(uriString);

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

        String body = response.body();

        System.out.println(body);

        ObjectMapper mapper = new ObjectMapper();

        GameResponse gameResponse;

        try {
            gameResponse = mapper.readValue(body, GameResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Game> games = gameResponse.games();

        return List.copyOf(games);
    }
}
