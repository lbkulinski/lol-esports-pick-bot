package net.lbku.dto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.lbku.dto.serialization.BooleanDeserializer;
import net.lbku.dto.serialization.InstantDeserializer;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
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
}
