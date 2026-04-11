package net.lbku.bluesky.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@NullMarked
public record BlueskySession(
    String handle,
    String accessJwt,
    String refreshJwt
) {
    public BlueskySession {
        Objects.requireNonNull(handle, "handle must not be null");
        Objects.requireNonNull(accessJwt, "accessJwt must not be null");
        Objects.requireNonNull(refreshJwt, "refreshJwt must not be null");
    }
}
