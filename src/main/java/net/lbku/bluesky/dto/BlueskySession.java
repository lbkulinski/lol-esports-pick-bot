package net.lbku.bluesky.dto;

import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public record BlueskySession(
    String accessJwt,
    String refreshJwt
) {
    public BlueskySession {
        Objects.requireNonNull(accessJwt, "accessJwt must not be null");
        Objects.requireNonNull(refreshJwt, "refreshJwt must not be null");
    }
}
