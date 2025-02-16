package net.lbku.service;

import com.google.inject.Inject;
import net.lbku.model.Champion;
import org.wikipedia.Wiki;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public final class PickService {
    private final Wiki wiki;

    @Inject
    public PickService(Wiki wiki) {
        this.wiki = Objects.requireNonNull(wiki);
    }

    public String getPicks(Champion champion) {
        Objects.requireNonNull(champion);

        Map<String, String> getParams = Map.of(
            "action", "cargoquery",
            "format", "json",
            "tables", "ScoreboardPlayers,ScoreboardGames",
            "join_on", "ScoreboardPlayers.GameId=ScoreboardGames.GameId",
            "fields", String.join(",", new String[]{
                "ScoreboardPlayers.GameId",
                "ScoreboardPlayers.Link",
                "ScoreboardGames.Tournament",
                "ScoreboardPlayers.DateTime_UTC",
                "ScoreboardPlayers.PlayerWin",
                "ScoreboardGames.VOD"
            }),
            "where", "ScoreboardPlayers.Champion='%s'".formatted(champion),
            "order_by", "ScoreboardPlayers.DateTime_UTC DESC",
            "limit", "500"
        );

        String response;

        try {
            response = this.wiki.makeApiCall(getParams, null, "CargoQueryDraven");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }
}
