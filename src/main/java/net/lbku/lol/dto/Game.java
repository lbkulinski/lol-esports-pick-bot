package net.lbku.lol.dto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import net.lbku.lol.dto.serialization.BooleanDeserializer;
import net.lbku.lol.dto.serialization.InstantDeserializer;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@NullMarked
public record Game(
    @JsonAlias("GameId")
    String id,

    @JsonAlias("Link")
    String player,

    @JsonAlias("Tournament")
    String tournament,

    @JsonAlias("DateTime UTC")
    @JsonDeserialize(using = InstantDeserializer.class)
    Instant timestamp,

    @JsonAlias("PlayerWin")
    @JsonDeserialize(using = BooleanDeserializer.class)
    boolean won,

    @JsonAlias("VOD")
    String vod
) {
    public Game {
        Objects.requireNonNull(id);
        Objects.requireNonNull(player);
        Objects.requireNonNull(tournament);
        Objects.requireNonNull(timestamp);
        Objects.requireNonNull(vod);
    }
}
