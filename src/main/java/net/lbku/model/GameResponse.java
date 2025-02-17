package net.lbku.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record GameResponse(
    @JsonAlias("cargoquery")
    List<Game> games
) {
}
