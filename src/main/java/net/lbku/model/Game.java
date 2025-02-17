package net.lbku.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.lbku.model.serialization.StringToBooleanConverter;
import net.lbku.model.serialization.StringToInstantConverter;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Game(
    @JsonAlias("GameId")
    String id,

    @JsonAlias("Link")
    String player,

    @JsonAlias("Tournament")
    String tournament,

    @JsonAlias("_DateTime.20.UTC")
    @JsonDeserialize(converter = StringToInstantConverter.class)
    Instant instant,

    @JsonAlias("PlayerWin")
    @JsonDeserialize(converter = StringToBooleanConverter.class)
    boolean won,

    @JsonAlias("VOD")
    String vod
) {
}
