package net.lbku.lol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@NullMarked
public record GameResponse(@JsonAlias("cargoquery") List<GameWrapper> gameWrappers) {
    public GameResponse {
        Objects.requireNonNull(gameWrappers, "gameWrappers must not be null");

        gameWrappers = List.copyOf(gameWrappers);
    }
}
