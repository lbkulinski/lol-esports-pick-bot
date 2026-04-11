package net.lbku.lol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@NullMarked
public record GameWrapper(@JsonAlias("title") Game game) {
    public GameWrapper {
        Objects.requireNonNull(game, "game must not be null");
    }
}
