package net.lbku.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.lbku.model.serialization.GameDeserializer;

import java.util.List;

public record GameResponse(
    @JsonAlias("cargoquery")
    @JsonDeserialize(contentUsing = GameDeserializer.class)
    List<Game> games
) {
}
