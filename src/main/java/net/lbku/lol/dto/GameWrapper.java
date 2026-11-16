package net.lbku.lol.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record GameWrapper(@JsonAlias("title") Game game) {
    public GameWrapper {
        Objects.requireNonNull(game);
    }
}
