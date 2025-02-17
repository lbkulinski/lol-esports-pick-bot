package net.lbku.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record GameBody(
    @JsonAlias("title")
    Game game
) {
}
